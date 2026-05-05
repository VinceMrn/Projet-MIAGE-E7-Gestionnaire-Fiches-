import { useState, useEffect } from 'react'
import * as api from '../../api/api'
import Portrait from '../modules/Portrait'
import Biographie from '../modules/Biographie'
import Statistiques from '../modules/Statistiques'
import Competences from '../modules/Competences'
import Equipements from '../modules/Equipements'

const MIN_W = 200
const MIN_H = 120

export default function FicheDetail({ idFiche, onRetour }) {
  const [fiche, setFiche] = useState(null)
  const [chargement, setChargement] = useState(true)
  const [erreur, setErreur] = useState('')
  const [suggestions, setSuggestions] = useState({ competences: [], equipements: [], statistiques: [] })
  const [positions, setPositions] = useState({})
  const [editMode, setEditMode] = useState(false)
  const [statut, setStatut] = useState('')

  const cinzel = "'Cinzel', serif"
  const crimson = "'Crimson Text', Georgia, serif"

  const initPositions = (data) => ({
    portrait:     { x: data.portrait.posX,     y: data.portrait.posY,     w: data.portrait.largeur,     h: data.portrait.hauteur },
    biographie:   { x: data.biographie.posX,   y: data.biographie.posY,   w: data.biographie.largeur,   h: data.biographie.hauteur },
    statistiques: { x: data.statistiques.posX, y: data.statistiques.posY, w: data.statistiques.largeur, h: data.statistiques.hauteur },
    competence:   { x: data.competences.posX,  y: data.competences.posY,  w: data.competences.largeur,  h: data.competences.hauteur },
    equipement:   { x: data.equipements.posX,  y: data.equipements.posY,  w: data.equipements.largeur,  h: data.equipements.hauteur },
  })

  const chargerFiche = async () => {
    setChargement(true)
    setErreur('')
    try {
      const data = await api.getFiche(idFiche)
      setFiche(data)
      setPositions(initPositions(data))
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

  const flashStatut = (msg) => {
    setStatut(msg)
    setTimeout(() => setStatut(''), 1500)
  }

  const persisterPosition = async (nom, p) => {
    try {
      await api.modifierPositionModule(idFiche, nom, p.x, p.y)
      flashStatut('Position sauvegardee')
    } catch { chargerFiche() }
  }

  const persisterTaille = async (nom, p) => {
    try {
      await api.modifierTailleModule(idFiche, nom, p.w, p.h)
      flashStatut('Taille sauvegardee')
    } catch { chargerFiche() }
  }

  const sauvegarderTout = async () => {
    try {
      for (const nom of Object.keys(positions)) {
        const p = positions[nom]
        await api.modifierPositionModule(idFiche, nom, p.x, p.y)
        await api.modifierTailleModule(idFiche, nom, p.w, p.h)
      }
      flashStatut('Disposition sauvegardee')
    } catch (e) { setErreur(e.message) }
  }

  if (chargement) return (
    <p style={{ color: '#6a5a3a', textAlign: 'center', fontStyle: 'italic', fontFamily: crimson, marginTop: 40 }}>
      Chargement du grimoire...
    </p>
  )

  if (erreur && !fiche) return (
    <div style={{ fontFamily: crimson }}>
      <button onClick={onRetour} style={btnGhost(cinzel)}>← Retour</button>
      <div style={{ background: '#4a1515', border: '1px solid #8a3030', color: '#f0a0a0', padding: '10px 14px', borderRadius: 6, fontSize: 13, marginTop: 16 }}>{erreur}</div>
    </div>
  )

  const canvasL = fiche?.canvas?.largeur ?? 1200
  const canvasH = fiche?.canvas?.hauteur ?? 700

  const startDrag = (nom, e) => {
    if (!editMode) return
    e.preventDefault()
    const startX = e.clientX, startY = e.clientY
    const init = positions[nom]
    const onMove = (ev) => {
      setPositions(prev => {
        const cur = prev[nom]
        const x = Math.max(0, Math.min(init.x + ev.clientX - startX, canvasL - cur.w))
        const y = Math.max(0, Math.min(init.y + ev.clientY - startY, canvasH - cur.h))
        return { ...prev, [nom]: { ...cur, x, y } }
      })
    }
    const onUp = () => {
      window.removeEventListener('mousemove', onMove)
      window.removeEventListener('mouseup', onUp)
      setPositions(prev => { persisterPosition(nom, prev[nom]); return prev })
    }
    window.addEventListener('mousemove', onMove)
    window.addEventListener('mouseup', onUp)
  }

  const startResize = (nom, e) => {
    if (!editMode) return
    e.preventDefault()
    e.stopPropagation()
    const startX = e.clientX, startY = e.clientY
    const init = positions[nom]
    const onMove = (ev) => {
      setPositions(prev => {
        const cur = prev[nom]
        const w = Math.max(MIN_W, Math.min(init.w + ev.clientX - startX, canvasL - cur.x))
        const h = Math.max(MIN_H, Math.min(init.h + ev.clientY - startY, canvasH - cur.y))
        return { ...prev, [nom]: { ...cur, w, h } }
      })
    }
    const onUp = () => {
      window.removeEventListener('mousemove', onMove)
      window.removeEventListener('mouseup', onUp)
      setPositions(prev => { persisterTaille(nom, prev[nom]); return prev })
    }
    window.addEventListener('mousemove', onMove)
    window.addEventListener('mouseup', onUp)
  }

  const renduModule = (nom, contenu) => {
    const p = positions[nom]
    if (!p) return null
    return (
      <div key={nom} style={{
        position: 'absolute', left: p.x, top: p.y, width: p.w, height: p.h,
        outline: editMode ? '1px dashed #8a6a3a' : 'none', borderRadius: 8,
        transition: 'outline 0.2s',
      }}>
        {editMode && (
          <>
            <div
              onMouseDown={(e) => startDrag(nom, e)}
              title="Glisser pour deplacer"
              style={{
                position: 'absolute', top: 6, left: 6, zIndex: 5,
                background: 'rgba(42,31,20,0.92)', border: '1px solid #8a6a3a',
                color: '#e8d5a0', fontFamily: cinzel, fontSize: 11,
                padding: '4px 8px', borderRadius: 4, cursor: 'move',
                letterSpacing: '0.06em', userSelect: 'none',
              }}
            >⠿ {nom}</div>
            <div
              onMouseDown={(e) => startResize(nom, e)}
              title="Glisser pour redimensionner"
              style={{
                position: 'absolute', right: 0, bottom: 0, zIndex: 5,
                width: 18, height: 18, cursor: 'nwse-resize',
                background: 'linear-gradient(135deg, transparent 50%, #8a6a3a 50%, #8a6a3a 70%, transparent 70%)',
                userSelect: 'none',
              }}
            />
          </>
        )}
        <div className="module-fill" style={{ width: '100%', height: '100%', overflow: 'auto', borderRadius: 8 }}>
          {contenu}
        </div>
      </div>
    )
  }


  return (
    <div style={{ fontFamily: crimson, color: '#d4c4a0', height: 'calc(100vh - 110px)', display: 'flex', flexDirection: 'column' }}>

      <style>{`
        @keyframes fadeUp { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }
        .grimoire-canvas::-webkit-scrollbar { width: 8px; height: 8px; }
        .grimoire-canvas::-webkit-scrollbar-track { background: #1a1208; }
        .grimoire-canvas::-webkit-scrollbar-thumb { background: #3a2c18; border-radius: 4px; }
        .grimoire-canvas::-webkit-scrollbar-thumb:hover { background: #5c4a2a; }
        .module-fill > * { width: 100% !important; height: 100% !important; box-sizing: border-box; }
      `}</style>

      <div style={{
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        gap: 16, paddingBottom: 14, marginBottom: 14,
        borderBottom: '1px solid #3a2c18',
        animation: 'fadeUp 0.4s ease-out',
      }}>
        <button onClick={onRetour} style={btnGhost(cinzel)}>← Retour aux fiches</button>

        <div style={{ flex: 1, textAlign: 'center' }}>
          <h1 style={{
            fontFamily: cinzel, fontSize: 22, fontWeight: 700, color: '#e8d5a0',
            margin: 0, letterSpacing: '0.14em', textTransform: 'uppercase',
            textShadow: '0 0 24px rgba(196, 168, 106, 0.35)',
          }}>{fiche.nom}</h1>
          {statut && (
            <p style={{ fontSize: 11, color: '#8aa86a', margin: '4px 0 0', letterSpacing: '0.04em' }}>{statut}</p>
          )}
        </div>

        <div style={{ display: 'flex', gap: 8 }}>
          <button onClick={() => setEditMode(m => !m)} style={editMode ? btnPrimary(cinzel) : btnGhost(cinzel)}>
            {editMode ? '✓ Terminer' : '✎ Reorganiser'}
          </button>
          {editMode && (
            <button onClick={sauvegarderTout} style={btnPrimary(cinzel)}>💾 Sauvegarder</button>
          )}
        </div>
      </div>

      <div className="grimoire-canvas" style={{
        flex: 1, minHeight: 0, overflow: 'auto', position: 'relative',
        width: '100%', maxWidth: canvasL, maxHeight: canvasH, margin: '0 auto',
        background: editMode
          ? 'repeating-linear-gradient(0deg, transparent, transparent 39px, #2a1f14 39px, #2a1f14 40px), repeating-linear-gradient(90deg, transparent, transparent 39px, #2a1f14 39px, #2a1f14 40px)'
          : 'transparent',
        border: editMode ? '1px solid #3a2c18' : 'none',
        borderRadius: 6,
      }}>
        <div style={{ position: 'relative', width: canvasL, height: canvasH }}>
          {renduModule('portrait',     <Portrait     portrait={fiche.portrait}         idFiche={idFiche} onUpdate={chargerFiche} />)}
          {renduModule('biographie',   <Biographie   biographie={fiche.biographie}     idFiche={idFiche} onUpdate={chargerFiche} />)}
          {renduModule('statistiques', <Statistiques statistiques={fiche.statistiques} idFiche={idFiche} onUpdate={rafraichirTout} suggestions={suggestions.statistiques} />)}
          {renduModule('competence',   <Competences  competences={fiche.competences}   idFiche={idFiche} onUpdate={rafraichirTout} suggestions={suggestions.competences} />)}
          {renduModule('equipement',   <Equipements  equipements={fiche.equipements}   idFiche={idFiche} onUpdate={rafraichirTout} suggestions={suggestions.equipements} />)}
        </div>
      </div>
    </div>
  )
}

function btnGhost(cinzel) {
  return {
    background: '#2a1f14', border: '1px solid #5c4a2a', color: '#c4a86a',
    fontFamily: cinzel, fontSize: 12, cursor: 'pointer',
    padding: '9px 16px', borderRadius: 6, fontWeight: 600,
    letterSpacing: '0.08em', textTransform: 'uppercase',
  }
}

function btnPrimary(cinzel) {
  return {
    background: '#4a7030', border: '1px solid #6a9040', color: '#c8e0a0',
    fontFamily: cinzel, fontSize: 12, cursor: 'pointer',
    padding: '9px 18px', borderRadius: 6, fontWeight: 600,
    letterSpacing: '0.08em', textTransform: 'uppercase',
  }
}
