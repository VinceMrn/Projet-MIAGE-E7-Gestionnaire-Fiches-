import { useState, useEffect } from 'react'
import * as api from '../../api/api'
import Portrait from '../modules/Portrait'
import Biographie from '../modules/Biographie'
import Statistiques from '../modules/Statistiques'
import Competences from '../modules/Competences'
import Equipements from '../modules/Equipements'

const MODULES = [
  { id: 'portrait',     label: 'Portrait',      icon: '◈' },
  { id: 'biographie',   label: 'Biographie',    icon: '✦' },
  { id: 'statistiques', label: 'Statistiques',  icon: '⚔' },
  { id: 'competences',  label: 'Compétences',   icon: '✧' },
  { id: 'equipements',  label: 'Équipements',   icon: '⚜' },
]

export default function FicheDetail({ idFiche, onRetour }) {
  const [fiche, setFiche] = useState(null)
  const [chargement, setChargement] = useState(true)
  const [erreur, setErreur] = useState('')
  const [sectionActive, setSectionActive] = useState('portrait')
  const [suggestions, setSuggestions] = useState({ competences: [], equipements: [], statistiques: [] })

  const cinzel = "'Cinzel', serif"
  const crimson = "'Crimson Text', Georgia, serif"

  const chargerFiche = async () => {
    setChargement(true)
    setErreur('')
    try {
      const data = await api.getFiche(idFiche)
      setFiche(data)
    } catch (err) {
      setErreur(err.message)
    } finally {
      setChargement(false)
    }
  }

  // Récupère compétences / équipements / stats uniques via le back (3 appels triviaux)
  const chargerSuggestions = async () => {
    try {
      const [competences, equipements, statistiques] = await Promise.all([
        api.listerCompetencesExistantes(),
        api.listerEquipementsExistants(),
        api.listerStatistiquesExistantes(),
      ])
      setSuggestions({ competences, equipements, statistiques })
    } catch (e) {
      console.error('Erreur chargement suggestions :', e)
    }
  }

  // Rafraîchit fiche + suggestions ensemble (utilisé après chaque ajout/modif)
  const rafraichirTout = async () => {
    await Promise.all([chargerFiche(), chargerSuggestions()])
  }

  useEffect(() => { chargerFiche(); chargerSuggestions() }, [idFiche])

  const renderModule = (mod) => {
    if (!fiche) return null
    switch (mod.id) {
      case 'portrait':     return <Portrait     portrait={fiche.portrait}         idFiche={idFiche} onUpdate={chargerFiche} />
      case 'biographie':   return <Biographie   biographie={fiche.biographie}     idFiche={idFiche} onUpdate={chargerFiche} />
      case 'statistiques': return <Statistiques statistiques={fiche.statistiques} idFiche={idFiche} onUpdate={rafraichirTout} suggestions={suggestions.statistiques} />
      case 'competences':  return <Competences  competences={fiche.competences}   idFiche={idFiche} onUpdate={rafraichirTout} suggestions={suggestions.competences} />
      case 'equipements':  return <Equipements  equipements={fiche.equipements}   idFiche={idFiche} onUpdate={rafraichirTout} suggestions={suggestions.equipements} />
      default: return null
    }
  }

  const scrollToSection = (id) => {
    setSectionActive(id)
    const el = document.getElementById(`section-${id}`)
    if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }

  if (chargement) return (
    <p style={{ color: '#6a5a3a', textAlign: 'center', fontStyle: 'italic', fontFamily: crimson, marginTop: 40 }}>
      Chargement du grimoire...
    </p>
  )

  if (erreur) return (
    <div style={{ fontFamily: crimson }}>
      <button
        onClick={onRetour}
        style={{ background: '#2a1f14', border: '1px solid #5c4a2a', color: '#c4a86a', fontFamily: cinzel, fontSize: 12, cursor: 'pointer', padding: '8px 14px', borderRadius: 6, marginBottom: 16, fontWeight: 600, letterSpacing: '0.06em', textTransform: 'uppercase' }}
      >
        ← Retour
      </button>
      <div style={{ background: '#4a1515', border: '1px solid #8a3030', color: '#f0a0a0', padding: '10px 14px', borderRadius: 6, fontSize: 13 }}>{erreur}</div>
    </div>
  )

  return (
    <div style={{ fontFamily: crimson, color: '#d4c4a0' }}>

      {/* Barre de navigation sticky en haut */}
      <div style={{
        position: 'sticky',
        top: 0,
        zIndex: 50,
        background: 'linear-gradient(180deg, #1a1208 0%, #1a1208 85%, rgba(26,18,8,0.95) 100%)',
        padding: '14px 0',
        marginBottom: 20,
        borderBottom: '1px solid #3a2c18',
        boxShadow: '0 4px 12px rgba(0,0,0,0.4)',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
          <button
            onClick={onRetour}
            style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: 6,
              background: '#2a1f14',
              border: '1px solid #5c4a2a',
              color: '#c4a86a',
              fontFamily: cinzel,
              fontSize: 12,
              cursor: 'pointer',
              padding: '9px 16px',
              borderRadius: 6,
              fontWeight: 600,
              letterSpacing: '0.08em',
              textTransform: 'uppercase',
              transition: 'all 0.2s',
            }}
            onMouseEnter={e => { e.currentTarget.style.background = '#3a2c18'; e.currentTarget.style.color = '#e8d5a0' }}
            onMouseLeave={e => { e.currentTarget.style.background = '#2a1f14'; e.currentTarget.style.color = '#c4a86a' }}
          >
            ← Retour aux fiches
          </button>

          <div style={{ flex: 1, textAlign: 'center' }}>
            <h1 style={{
              fontFamily: cinzel,
              fontSize: 22,
              fontWeight: 700,
              color: '#e8d5a0',
              margin: 0,
              letterSpacing: '0.12em',
              textTransform: 'uppercase',
              textShadow: '0 0 20px rgba(196, 168, 106, 0.3)',
            }}>
              {fiche.nom}
            </h1>
            {[fiche.classe, fiche.niveau && `Niveau ${fiche.niveau}`, fiche.alignement].filter(Boolean).length > 0 && (
              <p style={{ fontSize: 12, color: '#8a7a5a', fontStyle: 'italic', margin: '4px 0 0', letterSpacing: '0.04em' }}>
                {[fiche.classe, fiche.niveau && `Niveau ${fiche.niveau}`, fiche.alignement].filter(Boolean).join(' • ')}
              </p>
            )}
          </div>

          <button
            onClick={onRetour}
            style={{
              background: '#4a7030',
              border: '1px solid #6a9040',
              color: '#c8e0a0',
              fontFamily: cinzel,
              fontSize: 12,
              cursor: 'pointer',
              padding: '9px 18px',
              borderRadius: 6,
              fontWeight: 600,
              letterSpacing: '0.08em',
              textTransform: 'uppercase',
              transition: 'all 0.2s',
            }}
            onMouseEnter={e => { e.currentTarget.style.background = '#5a8038' }}
            onMouseLeave={e => { e.currentTarget.style.background = '#4a7030' }}
          >
            ✓ Valider
          </button>
        </div>

        {/* Onglets de navigation rapide entre modules */}
        <div style={{
          display: 'flex',
          gap: 4,
          marginTop: 14,
          paddingTop: 12,
          borderTop: '1px solid #2a1f14',
          overflowX: 'auto',
        }}>
          {MODULES.map(m => (
            <button
              key={m.id}
              onClick={() => scrollToSection(m.id)}
              style={{
                background: sectionActive === m.id ? '#3a2c18' : 'transparent',
                border: '1px solid',
                borderColor: sectionActive === m.id ? '#6a5a3a' : 'transparent',
                color: sectionActive === m.id ? '#e8d5a0' : '#8a7a5a',
                fontFamily: cinzel,
                fontSize: 11,
                cursor: 'pointer',
                padding: '6px 14px',
                borderRadius: 5,
                fontWeight: 600,
                letterSpacing: '0.06em',
                textTransform: 'uppercase',
                whiteSpace: 'nowrap',
                transition: 'all 0.15s',
                display: 'inline-flex',
                alignItems: 'center',
                gap: 6,
              }}
              onMouseEnter={e => {
                if (sectionActive !== m.id) {
                  e.currentTarget.style.color = '#c4b080'
                  e.currentTarget.style.borderColor = '#3a2c18'
                }
              }}
              onMouseLeave={e => {
                if (sectionActive !== m.id) {
                  e.currentTarget.style.color = '#8a7a5a'
                  e.currentTarget.style.borderColor = 'transparent'
                }
              }}
            >
              <span style={{ color: '#c4a86a' }}>{m.icon}</span>
              {m.label}
            </button>
          ))}
        </div>
      </div>

      {/* Modules — section principale */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: 18, paddingBottom: 60 }}>
        {MODULES.map(m => (
          <div key={m.id} id={`section-${m.id}`}>
            {renderModule(m)}
          </div>
        ))}
      </div>

      {/* Boutons d'action en bas de page */}
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        gap: 12,
        padding: '20px 0 40px',
        borderTop: '1px solid #3a2c18',
        marginTop: 20,
      }}>
        <button
          onClick={onRetour}
          style={{
            background: 'transparent',
            border: '1px solid #5c4a2a',
            color: '#8a7a5a',
            fontFamily: cinzel,
            fontSize: 12,
            cursor: 'pointer',
            padding: '10px 24px',
            borderRadius: 6,
            fontWeight: 600,
            letterSpacing: '0.08em',
            textTransform: 'uppercase',
            transition: 'all 0.2s',
          }}
          onMouseEnter={e => { e.currentTarget.style.borderColor = '#8a7a5a'; e.currentTarget.style.color = '#c4b080' }}
          onMouseLeave={e => { e.currentTarget.style.borderColor = '#5c4a2a'; e.currentTarget.style.color = '#8a7a5a' }}
        >
          ← Retour
        </button>
        <button
          onClick={onRetour}
          style={{
            background: '#4a7030',
            border: '1px solid #6a9040',
            color: '#c8e0a0',
            fontFamily: cinzel,
            fontSize: 12,
            cursor: 'pointer',
            padding: '10px 28px',
            borderRadius: 6,
            fontWeight: 600,
            letterSpacing: '0.08em',
            textTransform: 'uppercase',
            transition: 'all 0.2s',
          }}
          onMouseEnter={e => { e.currentTarget.style.background = '#5a8038' }}
          onMouseLeave={e => { e.currentTarget.style.background = '#4a7030' }}
        >
          ✓ Valider les modifications
        </button>
      </div>
    </div>
  )
}
