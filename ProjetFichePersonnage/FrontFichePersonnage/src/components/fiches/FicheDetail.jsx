import { useState, useEffect } from 'react'
import * as api from '../../api/api'
import Portrait from '../modules/Portrait'
import Biographie from '../modules/Biographie'
import Statistiques from '../modules/Statistiques'
import Competences from '../modules/Competences'
import Equipements from '../modules/Equipements'

export default function FicheDetail({ idFiche, onRetour }) {
  const [fiche, setFiche] = useState(null)
  const [chargement, setChargement] = useState(true)
  const [erreur, setErreur] = useState('')
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

  const rafraichirTout = async () => {
    await Promise.all([chargerFiche(), chargerSuggestions()])
  }

  useEffect(() => { chargerFiche(); chargerSuggestions() }, [idFiche])

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

  // styles communs
  const btnGhost = {
    display: 'inline-flex', alignItems: 'center', gap: 6,
    background: '#2a1f14', border: '1px solid #5c4a2a', color: '#c4a86a',
    fontFamily: cinzel, fontSize: 12, cursor: 'pointer',
    padding: '9px 16px', borderRadius: 6, fontWeight: 600,
    letterSpacing: '0.08em', textTransform: 'uppercase',
    transition: 'all 0.2s',
  }

  const btnPrimary = {
    background: '#4a7030', border: '1px solid #6a9040', color: '#c8e0a0',
    fontFamily: cinzel, fontSize: 12, cursor: 'pointer',
    padding: '9px 18px', borderRadius: 6, fontWeight: 600,
    letterSpacing: '0.08em', textTransform: 'uppercase',
    transition: 'all 0.2s',
  }

  const cellule = (delay) => ({
    minHeight: 0,
    minWidth: 0,
    overflowY: 'auto',
    animation: `fadeUp 0.5s ${delay}s ease-out backwards`,
  })

  return (
    <div style={{ fontFamily: crimson, color: '#d4c4a0', height: 'calc(100vh - 110px)', display: 'flex', flexDirection: 'column' }}>

      {/* keyframes pour animations subtiles */}
      <style>{`
        @keyframes fadeUp {
          from { opacity: 0; transform: translateY(8px); }
          to { opacity: 1; transform: translateY(0); }
        }
        .grimoire-cell::-webkit-scrollbar { width: 6px; }
        .grimoire-cell::-webkit-scrollbar-track { background: transparent; }
        .grimoire-cell::-webkit-scrollbar-thumb { background: #3a2c18; border-radius: 3px; }
        .grimoire-cell::-webkit-scrollbar-thumb:hover { background: #5c4a2a; }
      `}</style>

      {/* En-tête compact */}
      <div style={{
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        gap: 16, paddingBottom: 14, marginBottom: 14,
        borderBottom: '1px solid #3a2c18',
        animation: 'fadeUp 0.4s ease-out',
      }}>
        <button
          onClick={onRetour}
          style={btnGhost}
          onMouseEnter={e => { e.currentTarget.style.background = '#3a2c18'; e.currentTarget.style.color = '#e8d5a0' }}
          onMouseLeave={e => { e.currentTarget.style.background = '#2a1f14'; e.currentTarget.style.color = '#c4a86a' }}
        >
          ← Retour aux fiches
        </button>

        <div style={{ flex: 1, textAlign: 'center' }}>
          <h1 style={{
            fontFamily: cinzel, fontSize: 22, fontWeight: 700, color: '#e8d5a0',
            margin: 0, letterSpacing: '0.14em', textTransform: 'uppercase',
            textShadow: '0 0 24px rgba(196, 168, 106, 0.35)',
          }}>
            {fiche.nom}
          </h1>
          {[fiche.classe, fiche.niveau && `Niveau ${fiche.niveau}`, fiche.alignement].filter(Boolean).length > 0 && (
            <p style={{ fontSize: 12, color: '#8a7a5a', fontStyle: 'italic', margin: '3px 0 0', letterSpacing: '0.04em' }}>
              {[fiche.classe, fiche.niveau && `Niveau ${fiche.niveau}`, fiche.alignement].filter(Boolean).join(' • ')}
            </p>
          )}
        </div>

        <button
          onClick={onRetour}
          style={btnPrimary}
          onMouseEnter={e => { e.currentTarget.style.background = '#5a8038'; e.currentTarget.style.transform = 'translateY(-1px)' }}
          onMouseLeave={e => { e.currentTarget.style.background = '#4a7030'; e.currentTarget.style.transform = 'translateY(0)' }}
        >
          ✓ Valider
        </button>
      </div>

      {/* Grille : tout visible sans scroll de page */}
      <div style={{
        flex: 1,
        minHeight: 0,
        display: 'grid',
        gap: 14,
        gridTemplateColumns: '320px 1fr 1fr',
        gridTemplateRows: '1fr 1fr',
        gridTemplateAreas: `
          "portrait    stats   competences"
          "biographie  stats   equipements"
        `,
      }}>
        <div className="grimoire-cell" style={{ gridArea: 'portrait',    ...cellule(0.05) }}>
          <Portrait     portrait={fiche.portrait}         idFiche={idFiche} onUpdate={chargerFiche} />
        </div>

        <div className="grimoire-cell" style={{ gridArea: 'biographie',  ...cellule(0.10) }}>
          <Biographie   biographie={fiche.biographie}     idFiche={idFiche} onUpdate={chargerFiche} />
        </div>

        <div className="grimoire-cell" style={{ gridArea: 'stats',       ...cellule(0.15) }}>
          <Statistiques statistiques={fiche.statistiques} idFiche={idFiche} onUpdate={rafraichirTout} suggestions={suggestions.statistiques} />
        </div>

        <div className="grimoire-cell" style={{ gridArea: 'competences', ...cellule(0.20) }}>
          <Competences  competences={fiche.competences}   idFiche={idFiche} onUpdate={rafraichirTout} suggestions={suggestions.competences} />
        </div>

        <div className="grimoire-cell" style={{ gridArea: 'equipements', ...cellule(0.25) }}>
          <Equipements  equipements={fiche.equipements}   idFiche={idFiche} onUpdate={rafraichirTout} suggestions={suggestions.equipements} />
        </div>
      </div>
    </div>
  )
}
