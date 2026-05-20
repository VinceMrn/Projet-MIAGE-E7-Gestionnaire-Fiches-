import { useState, useEffect, useRef } from 'react'
import * as api from '../../api/api'
import * as combatApi from '../../api/combat'
import BarreVie from './BarreVie'

const cinzel = "'Cinzel', serif"
const crimson = "'Crimson Text', Georgia, serif"

const HP_JOUEUR_MAX = 100

// Les 5 compétences codées en dur dans le jeu.
// "mot" est le mot-clé cherché dans les compétences de la fiche (insensible aux accents).
const COMPETENCES_JEU = [
  { cle: 'rage',      nom: 'Rage',        mot: 'rage',     description: 'Dégâts ×2 ce tour',                  cooldownMax: 2, couleur: '#e06030', bg: '#3a1505' },
  { cle: 'soin',      nom: 'Soin',        mot: 'soin',     description: 'Soigne 20 PV',                        cooldownMax: 3, couleur: '#40c060', bg: '#053a10' },
  { cle: 'bouclier',  nom: 'Bouclier',    mot: 'bouclier', description: '+5 DEF ce tour',                      cooldownMax: 2, couleur: '#4080e0', bg: '#05183a' },
  { cle: 'precision', nom: 'Coup Précis', mot: 'precis',   description: 'Prochain coup garanti critique',       cooldownMax: 3, couleur: '#e0c030', bg: '#3a2c05' },
  { cle: 'esquive',   nom: 'Esquive',     mot: 'esquive',  description: 'Annule la prochaine attaque ennemie',  cooldownMax: 3, couleur: '#a060e0', bg: '#2a0840' },
]

// Normalise une chaîne pour comparer sans tenir compte des accents ni de la casse
// Ex: "Précis" → "precis", "Évasion" → "evasion"
function normaliser(str) {
  return str.toLowerCase()
    .replace(/[éèêë]/g, 'e').replace(/[àâä]/g, 'a')
    .replace(/[ùûü]/g, 'u').replace(/[îï]/g, 'i')
    .replace(/[ôö]/g, 'o').replace(/ç/g, 'c')
}

// Retourne la couleur CSS d'une ligne de log selon les tags qu'elle contient
function couleurLigne(ligne) {
  if (ligne.includes('[CRITIQUE !]'))    return '#f0c040'
  if (ligne.includes('[ECHEC CRITIQUE]')) return '#f04040'
  if (ligne.includes('[RAGE]'))           return '#f07020'
  if (ligne.includes('[COUP PRÉCIS]'))   return '#f0d060'
  if (ligne.includes('[BOUCLIER]'))      return '#6090e0'
  if (ligne.includes('[ESQUIVE]'))       return '#a060e0'
  if (ligne.includes('[SOIN]'))          return '#40c060'
  if (ligne.includes('Victoire'))        return '#c8e0a0'
  if (ligne.includes('été vaincu'))      return '#f08060'
  return '#b0a080'
}

export default function CombatPage({ onRetour }) {
  // ─── Navigation entre phases ───────────────────────────────────────────────
  const [phase, setPhase] = useState('preparation') // 'preparation' | 'combat' | 'fin'

  // ─── Phase préparation ─────────────────────────────────────────────────────
  const [fiches, setFiches] = useState([])
  const [ficheSelectionnee, setFicheSelectionnee] = useState(null)
  const [statsDisponibles, setStatsDisponibles] = useState([])
  const [statAttaque, setStatAttaque] = useState(null)
  const [statDefense, setStatDefense] = useState(null)
  const [niveauEnnemi, setNiveauEnnemi] = useState(1)

  // ─── Phase combat ──────────────────────────────────────────────────────────
  const [ennemi, setEnnemi] = useState(null)
  const [hpJoueur, setHpJoueur] = useState(HP_JOUEUR_MAX)
  const [hpEnnemi, setHpEnnemi] = useState(0)
  const [hpEnnemMax, setHpEnnemMax] = useState(0)
  const [log, setLog] = useState([])
  const [chargement, setChargement] = useState(false)
  const [resultatFin, setResultatFin] = useState('')

  // ─── Compétences ───────────────────────────────────────────────────────────
  // Liste des compétences disponibles pour la fiche choisie (sous-ensemble de COMPETENCES_JEU)
  const [competencesActives, setCompetencesActives] = useState([])
  // Cooldown restant par compétence (en nombre de tours)
  const [cooldowns, setCooldowns] = useState({})
  // Effets temporaires en attente d'être appliqués au prochain tour
  const [effets, setEffets] = useState({ rage: false, bouclier: false, precision: false, esquive: false })

  const logRef = useRef(null)

  // Charger les fiches au montage
  useEffect(() => {
    api.listerFiches()
      .then(data => setFiches(Array.isArray(data) ? data : []))
      .catch(() => {})
  }, [])

  // Auto-scroll du log vers le bas à chaque nouveau message
  useEffect(() => {
    if (logRef.current) logRef.current.scrollTop = logRef.current.scrollHeight
  }, [log])

  // Quand une fiche est sélectionnée : charger ses stats et filtrer les compétences disponibles
  const handleSelectFiche = async (id) => {
    if (!id) return
    try {
      const fiche = await api.getFiche(Number(id))
      setFicheSelectionnee(fiche)

      const stats = fiche.statistiques?.liste || []
      setStatsDisponibles(stats)
      setStatAttaque(stats[0] || null)
      setStatDefense(stats[0] || null)

      // Filtrer les compétences : garder celles dont le mot-clé apparaît dans les compétences de la fiche
      const comptsFiche = fiche.competences?.liste || []
      const disponibles = COMPETENCES_JEU.filter(c =>
        comptsFiche.some(nom => normaliser(nom).includes(normaliser(c.mot)))
      )
      setCompetencesActives(disponibles)

      // Initialiser les cooldowns à 0 pour chaque compétence disponible
      const cds = {}
      disponibles.forEach(c => { cds[c.cle] = 0 })
      setCooldowns(cds)
    } catch {
      setStatsDisponibles([])
      setCompetencesActives([])
    }
  }

  // Lancer le combat : générer l'ennemi et passer en phase combat
  const handleCommencer = async () => {
    if (!statAttaque || !statDefense) return
    setChargement(true)
    try {
      const e = await combatApi.genererEnnemi(niveauEnnemi)
      setEnnemi(e)
      setHpEnnemi(e.hp)
      setHpEnnemMax(e.hp)
      setHpJoueur(HP_JOUEUR_MAX)
      setLog([`Le combat commence ! Vous affrontez ${e.nom} (ATK:${e.attaque} | DEF:${e.defense})`])
      // Réinitialiser les effets et cooldowns pour ce nouveau combat
      setEffets({ rage: false, bouclier: false, precision: false, esquive: false })
      const cds = {}
      competencesActives.forEach(c => { cds[c.cle] = 0 })
      setCooldowns(cds)
      setPhase('combat')
    } catch {
      setLog(['Erreur : impossible de joindre le serveur.'])
    } finally {
      setChargement(false)
    }
  }

  // Exécuter un tour d'attaque
  const handleAttaquer = async () => {
    if (chargement || phase !== 'combat') return

    // Capturer l'état des effets AVANT l'appel API (les setState sont asynchrones)
    const rageActif      = effets.rage
    const bouclierActif  = effets.bouclier
    const precisionActif = effets.precision
    const esquiveActif   = effets.esquive
    const bonusDefense   = bouclierActif ? 5 : 0

    setChargement(true)
    try {
      const res = await combatApi.attaquer(
        statAttaque.valeur,
        statDefense.valeur,
        bonusDefense,
        ennemi.defense,
        ennemi.attaque
      )

      let finalDegatsJoueur = res.degatsJoueur
      let finalDegatsEnnemi = res.degatsEnnemi
      const logTour = [res.description]

      // ── Appliquer les effets actifs ────────────────────────────────────────

      // Rage : dégâts du joueur ×2
      if (rageActif) {
        finalDegatsJoueur = finalDegatsJoueur * 2
        logTour.push(`[RAGE] Coup de rage ! ${finalDegatsJoueur} dégâts infligés.`)
      }

      // Coup Précis : garantit des dégâts (minimum = valeur stat) puis ×2
      if (precisionActif) {
        const baseGarantie = Math.max(finalDegatsJoueur, statAttaque.valeur)
        finalDegatsJoueur = baseGarantie * 2
        logTour.push(`[COUP PRÉCIS] Frappe assurée ! ${finalDegatsJoueur} dégâts critiques garantis.`)
      }

      // Bouclier : simplement loggé (déjà envoyé à Java via bonusDefense)
      if (bouclierActif) {
        logTour.push(`[BOUCLIER] Votre bouclier absorbe une partie des dégâts ! (+5 DEF)`)
      }

      // Esquive : annule complètement les dégâts de l'ennemi
      if (esquiveActif) {
        finalDegatsEnnemi = 0
        logTour.push(`[ESQUIVE] Vous esquivez l'attaque de ${ennemi.nom} !`)
      }

      // Décrémentation des cooldowns : -1 par tour pour chaque compétence
      setCooldowns(prev => {
        const updated = { ...prev }
        Object.keys(updated).forEach(k => { if (updated[k] > 0) updated[k]-- })
        return updated
      })

      // Réinitialiser les effets consommés ce tour
      setEffets({ rage: false, bouclier: false, precision: false, esquive: false })

      const nouveauHpEnnemi = Math.max(0, hpEnnemi - finalDegatsJoueur)
      const nouveauHpJoueur = Math.max(0, hpJoueur - finalDegatsEnnemi)
      setHpEnnemi(nouveauHpEnnemi)
      setHpJoueur(nouveauHpJoueur)
      setLog(prev => [...prev, ...logTour])

      if (nouveauHpEnnemi <= 0) {
        setLog(prev => [...prev, `Victoire ! Vous avez vaincu ${ennemi.nom} !`])
        setResultatFin('victoire')
        setPhase('fin')
      } else if (nouveauHpJoueur <= 0) {
        setLog(prev => [...prev, `Vous avez été vaincu par ${ennemi.nom}...`])
        setResultatFin('defaite')
        setPhase('fin')
      }
    } catch {
      setLog(prev => [...prev, 'Erreur de connexion au serveur.'])
    } finally {
      setChargement(false)
    }
  }

  // Activer une compétence (effet immédiat pour Soin, effet différé pour les autres)
  const handleCompetence = (cle) => {
    if ((cooldowns[cle] || 0) > 0 || chargement) return

    switch (cle) {
      case 'rage':
        setEffets(prev => ({ ...prev, rage: true }))
        setCooldowns(prev => ({ ...prev, rage: COMPETENCES_JEU.find(c => c.cle === 'rage').cooldownMax }))
        setLog(prev => [...prev, '[RAGE] Vous entrez en rage ! Prochains dégâts ×2.'])
        break
      case 'soin':
        setHpJoueur(prev => Math.min(HP_JOUEUR_MAX, prev + 20))
        setCooldowns(prev => ({ ...prev, soin: COMPETENCES_JEU.find(c => c.cle === 'soin').cooldownMax }))
        setLog(prev => [...prev, '[SOIN] Vous vous soignez de 20 PV.'])
        break
      case 'bouclier':
        setEffets(prev => ({ ...prev, bouclier: true }))
        setCooldowns(prev => ({ ...prev, bouclier: COMPETENCES_JEU.find(c => c.cle === 'bouclier').cooldownMax }))
        setLog(prev => [...prev, '[BOUCLIER] Vous levez votre bouclier ! +5 DEF au prochain tour.'])
        break
      case 'precision':
        setEffets(prev => ({ ...prev, precision: true }))
        setCooldowns(prev => ({ ...prev, precision: COMPETENCES_JEU.find(c => c.cle === 'precision').cooldownMax }))
        setLog(prev => [...prev, '[COUP PRÉCIS] Vous visez avec soin. Prochain coup = critique garanti.'])
        break
      case 'esquive':
        setEffets(prev => ({ ...prev, esquive: true }))
        setCooldowns(prev => ({ ...prev, esquive: COMPETENCES_JEU.find(c => c.cle === 'esquive').cooldownMax }))
        setLog(prev => [...prev, "[ESQUIVE] Vous vous préparez à esquiver la prochaine attaque !"])
        break
    }
  }

  const handleFuir = () => {
    setLog(prev => [...prev, `Vous fuyez devant ${ennemi?.nom}.`])
    setResultatFin('fuite')
    setPhase('fin')
  }

  const handleRejouer = () => {
    setPhase('preparation')
    setEnnemi(null)
    setLog([])
    setResultatFin('')
    setHpJoueur(HP_JOUEUR_MAX)
    setEffets({ rage: false, bouclier: false, precision: false, esquive: false })
    const cds = {}
    competencesActives.forEach(c => { cds[c.cle] = 0 })
    setCooldowns(cds)
  }

  // ─── Styles ────────────────────────────────────────────────────────────────
  const s = {
    container:   { fontFamily: crimson, color: '#d4c4a0', maxWidth: 700, margin: '0 auto' },
    titre:       { fontFamily: cinzel, fontSize: 22, color: '#e8d5a0', fontWeight: 600, marginBottom: 24 },
    label:       { fontFamily: cinzel, fontSize: 11, color: '#8a7050', display: 'block', marginBottom: 5, letterSpacing: '0.06em', textTransform: 'uppercase' },
    select:      { width: '100%', background: '#110d05', border: '1px solid #4a3a1a', color: '#d4c4a0', padding: '10px 14px', borderRadius: 6, fontFamily: crimson, fontSize: 15, outline: 'none', marginBottom: 18, cursor: 'pointer' },
    btnVert:     { background: '#4a7030', border: '1px solid #6a9040', color: '#c8e0a0', padding: '10px 26px', borderRadius: 6, fontFamily: cinzel, fontSize: 13, cursor: 'pointer', fontWeight: 600 },
    btnRouge:    { background: '#7a1515', border: '1px solid #a03030', color: '#f0c0b0', padding: '10px 22px', borderRadius: 6, fontFamily: cinzel, fontSize: 13, cursor: 'pointer', fontWeight: 600 },
    btnGris:     { background: 'transparent', border: '1px solid #4a3a1a', color: '#8a7a5a', padding: '10px 22px', borderRadius: 6, fontFamily: cinzel, fontSize: 12, cursor: 'pointer' },
    panelInfo:   { background: '#110d05', border: '1px solid #3a2c18', borderRadius: 8, padding: '10px 16px', marginBottom: 14, fontSize: 13, display: 'flex', gap: 18, flexWrap: 'wrap', alignItems: 'center' },
    infoLabel:   { color: '#6a5a3a' },
    infoVal:     { color: '#d4c4a0', fontFamily: cinzel, fontSize: 12, marginLeft: 4 },
    log:         { background: '#0d0a03', border: '1px solid #2e2410', borderRadius: 6, padding: '10px 14px', height: 160, overflowY: 'auto', fontSize: 13, fontFamily: crimson, lineHeight: 1.6 },
    logLine:     { paddingBottom: 3, marginBottom: 3, borderBottom: '1px solid #141008' },
    actions:     { display: 'flex', gap: 10, marginTop: 14, flexWrap: 'wrap' },
    compSection: { margin: '14px 0', padding: '12px 14px', background: '#0d0a03', border: '1px solid #2e2410', borderRadius: 8 },
    compTitre:   { fontFamily: cinzel, fontSize: 11, color: '#6a5a3a', letterSpacing: '0.08em', textTransform: 'uppercase', marginBottom: 10 },
    compGrid:    { display: 'flex', gap: 8, flexWrap: 'wrap' },
    finBox:      { textAlign: 'center', padding: '32px 0 20px' },
    finTitre:    { fontFamily: cinzel, fontSize: 30, fontWeight: 600, marginBottom: 8 },
    finSous:     { fontSize: 15, color: '#8a7050', marginBottom: 26 },
    vide:        { color: '#5a4a2a', fontStyle: 'italic', fontSize: 14 },
  }

  // ─── PHASE PRÉPARATION ──────────────────────────────────────────────────────
  if (phase === 'preparation') {
    return (
      <div style={s.container}>
        <div style={s.titre}>Combat — Préparation</div>

        <label style={s.label}>Choisir un personnage</label>
        <select style={s.select} defaultValue="" onChange={e => handleSelectFiche(e.target.value)}>
          <option value="" disabled>-- Sélectionner une fiche --</option>
          {fiches.map(f => <option key={f.id} value={f.id}>{f.nom}</option>)}
        </select>
        {fiches.length === 0 && <p style={s.vide}>Aucune fiche disponible. Créez d'abord un personnage.</p>}

        {statsDisponibles.length > 0 && (
          <>
            <label style={s.label}>Statistique d'attaque</label>
            <select style={s.select} onChange={e => {
              const trouvee = statsDisponibles.find(st => st.id === Number(e.target.value))
              setStatAttaque(trouvee || null)
            }}>
              {statsDisponibles.map(st => (
                <option key={st.id} value={st.id}>{st.nom} — {st.valeur}</option>
              ))}
            </select>

            <label style={s.label}>Statistique de défense</label>
            <select style={s.select} onChange={e => {
              const trouvee = statsDisponibles.find(st => st.id === Number(e.target.value))
              setStatDefense(trouvee || null)
            }}>
              {statsDisponibles.map(st => (
                <option key={st.id} value={st.id}>{st.nom} — {st.valeur}</option>
              ))}
            </select>

            <label style={s.label}>Niveau de l'ennemi</label>
            <select style={s.select} value={niveauEnnemi} onChange={e => setNiveauEnnemi(Number(e.target.value))}>
              <option value={1}>Niveau 1 — Facile</option>
              <option value={2}>Niveau 2 — Normal</option>
              <option value={3}>Niveau 3 — Difficile</option>
              <option value={4}>Niveau 4 — Épique</option>
              <option value={5}>Niveau 5 — Légendaire</option>
            </select>

            {competencesActives.length > 0 && (
              <div style={{ marginBottom: 18, padding: '10px 14px', background: '#0d0a03', border: '1px solid #2e2410', borderRadius: 8 }}>
                <div style={{ fontFamily: cinzel, fontSize: 11, color: '#6a5a3a', letterSpacing: '0.08em', textTransform: 'uppercase', marginBottom: 8 }}>
                  Compétences détectées dans la fiche
                </div>
                <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
                  {competencesActives.map(c => (
                    <span key={c.cle} style={{ fontFamily: cinzel, fontSize: 11, color: c.couleur, background: c.bg, border: `1px solid ${c.couleur}`, padding: '3px 10px', borderRadius: 4 }}>
                      {c.nom}
                    </span>
                  ))}
                </div>
              </div>
            )}

            <div style={s.actions}>
              <button style={s.btnVert} onClick={handleCommencer} disabled={chargement || !statAttaque || !statDefense}>
                {chargement ? 'Chargement...' : 'Commencer le Combat'}
              </button>
              <button style={s.btnGris} onClick={onRetour}>Retour</button>
            </div>
          </>
        )}

        {statsDisponibles.length === 0 && ficheSelectionnee && (
          <p style={s.vide}>Cette fiche n'a aucune statistique. Ajoutez-en une depuis la fiche.</p>
        )}
        {!ficheSelectionnee && fiches.length > 0 && (
          <div style={{ marginTop: 24 }}>
            <button style={s.btnGris} onClick={onRetour}>Retour</button>
          </div>
        )}
      </div>
    )
  }

  // ─── PHASE FIN ─────────────────────────────────────────────────────────────
  if (phase === 'fin') {
    const couleurTitre = resultatFin === 'victoire' ? '#c8e0a0' : resultatFin === 'defaite' ? '#f08060' : '#a09070'
    const textesTitre  = { victoire: 'Victoire !', defaite: 'Défaite...', fuite: 'Fuite !' }
    const textesSous   = {
      victoire: `Vous avez terrassé ${ennemi?.nom} !`,
      defaite:  `${ennemi?.nom} vous a mis hors combat.`,
      fuite:    `Vous avez fui devant ${ennemi?.nom}.`
    }

    return (
      <div style={s.container}>
        <div style={s.finBox}>
          <div style={{ ...s.finTitre, color: couleurTitre }}>{textesTitre[resultatFin]}</div>
          <div style={s.finSous}>{textesSous[resultatFin]}</div>
          <div style={{ ...s.actions, justifyContent: 'center' }}>
            <button style={s.btnVert} onClick={handleRejouer}>Rejouer</button>
            <button style={s.btnGris} onClick={onRetour}>Retour</button>
          </div>
        </div>
        <div style={s.log} ref={logRef}>
          {log.map((ligne, i) => (
            <div key={i} style={{ ...s.logLine, color: couleurLigne(ligne) }}>{ligne}</div>
          ))}
        </div>
      </div>
    )
  }

  // ─── PHASE COMBAT ──────────────────────────────────────────────────────────
  const defenseJoueurAffichee = 10 + Math.floor((statDefense?.valeur || 0) / 2)

  return (
    <div style={s.container}>
      <div style={s.titre}>Combat</div>

      {/* Barres de vie */}
      <BarreVie label={ficheSelectionnee?.nom || 'Joueur'} hp={hpJoueur} hpMax={HP_JOUEUR_MAX} couleur="#4a7030" />
      <BarreVie label={ennemi.nom} hp={hpEnnemi} hpMax={hpEnnemMax} couleur="#8a3020" />

      {/* Panneau d'informations */}
      <div style={s.panelInfo}>
        <span style={s.infoLabel}>Ennemi :<span style={s.infoVal}>{ennemi.nom}</span></span>
        <span style={s.infoLabel}>ATK ennemi :<span style={s.infoVal}>{ennemi.attaque}</span></span>
        <span style={s.infoLabel}>DEF ennemi :<span style={s.infoVal}>{ennemi.defense}</span></span>
        <span style={s.infoLabel}>Votre ATK :<span style={s.infoVal}>{statAttaque?.nom} ({statAttaque?.valeur})</span></span>
        <span style={s.infoLabel}>Votre DEF :<span style={s.infoVal}>{statDefense?.nom} → {defenseJoueurAffichee} (10+{statDefense?.valeur}/2)</span></span>
      </div>

      {/* Section compétences (affichée seulement si la fiche en a) */}
      {competencesActives.length > 0 && (
        <div style={s.compSection}>
          <div style={s.compTitre}>Compétences</div>
          <div style={s.compGrid}>
            {competencesActives.map(c => {
              const cd    = cooldowns[c.cle] || 0
              const actif = effets[c.cle]
              return (
                <button
                  key={c.cle}
                  onClick={() => handleCompetence(c.cle)}
                  disabled={cd > 0 || chargement}
                  style={{
                    background:    cd > 0 ? '#1a1005' : c.bg,
                    border:        `1px solid ${cd > 0 ? '#3a2c18' : c.couleur}`,
                    color:         cd > 0 ? '#4a3a1a' : c.couleur,
                    padding:       '7px 12px',
                    borderRadius:  6,
                    fontFamily:    cinzel,
                    fontSize:      11,
                    cursor:        cd > 0 ? 'not-allowed' : 'pointer',
                    opacity:       cd > 0 ? 0.5 : 1,
                    minWidth:      115,
                    textAlign:     'left',
                    transition:    'opacity 0.2s',
                    outline:       actif ? `2px solid ${c.couleur}` : 'none',
                  }}
                >
                  <div style={{ fontWeight: 600 }}>
                    {c.nom}{cd > 0 ? ` (${cd})` : actif ? ' ✓' : ''}
                  </div>
                  <div style={{ fontSize: 10, marginTop: 3, opacity: 0.8 }}>{c.description}</div>
                </button>
              )
            })}
          </div>
        </div>
      )}

      {/* Journal de combat */}
      <div style={s.log} ref={logRef}>
        {log.map((ligne, i) => (
          <div key={i} style={{ ...s.logLine, color: couleurLigne(ligne) }}>{ligne}</div>
        ))}
      </div>

      {/* Boutons d'action */}
      <div style={s.actions}>
        <button style={s.btnRouge} onClick={handleAttaquer} disabled={chargement}>
          {chargement ? '...' : `Attaquer (D20 + ${statAttaque?.valeur})`}
        </button>
        <button style={s.btnGris} onClick={handleFuir}>Fuir</button>
      </div>
    </div>
  )
}
