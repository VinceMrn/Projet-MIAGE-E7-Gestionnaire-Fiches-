import { useState } from 'react'
import * as api from '../../api/api'

export default function Competences({ competences, idFiche, onUpdate }) {
  const [nom, setNom] = useState('')
  const [erreur, setErreur] = useState('')
  const [ajout, setAjout] = useState(false)
  const [collapsed, setCollapsed] = useState(false)

  const handleAjouter = async (e) => {
    e.preventDefault()
    setErreur('')
    try {
      await api.ajouterCompetence(idFiche, nom)
      setNom('')
      setAjout(false)
      onUpdate()
    } catch (err) {
      setErreur(err.message)
    }
  }

  const cinzel = "'Cinzel', serif"
  const crimson = "'Crimson Text', Georgia, serif"

  return (
    <div style={{ background: '#2a1f14', border: '1px solid #5c4a2a', borderRadius: 8, overflow: 'hidden', fontFamily: crimson }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '12px 16px', borderBottom: collapsed ? 'none' : '1px solid #3a2c18' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, paddingLeft: 36 }}>
          <h3 style={{ fontFamily: cinzel, fontSize: 13, fontWeight: 600, color: '#e8d5a0', margin: 0, letterSpacing: '0.05em', textTransform: 'uppercase' }}>Skills & Abilities</h3>
        </div>
        <div style={{ display: 'flex', gap: 10 }}>
          <button onClick={() => setCollapsed(!collapsed)} style={{ background: 'transparent', border: 'none', color: '#6a5a3a', cursor: 'pointer', fontSize: 14 }}>{collapsed ? '∨' : '∧'}</button>
          <button style={{ background: 'transparent', border: 'none', color: '#6a5a3a', cursor: 'pointer', fontSize: 16 }}>⋮</button>
        </div>
      </div>

      {!collapsed && (
        <div style={{ padding: '16px' }}>
          {erreur && <div style={{ background: '#4a1515', border: '1px solid #8a3030', color: '#f0a0a0', padding: '8px 12px', borderRadius: 5, fontSize: 12, marginBottom: 12 }}>{erreur}</div>}

          {competences.liste.length === 0 ? (
            <p style={{ color: '#5a4a2a', fontStyle: 'italic', fontSize: 13 }}>Aucune compétence</p>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
              {competences.liste.map((comp, i) => (
                <div key={i} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', background: '#1e1509', border: '1px solid #3a2c18', borderRadius: 5, padding: '8px 12px' }}>
                  <span style={{ fontSize: 13, color: '#b0a080' }}>{comp.nom ?? comp}</span>
                  {comp.valeur !== undefined && <span style={{ fontSize: 13, color: '#c4a86a', fontWeight: 600 }}>{comp.valeur}</span>}
                </div>
              ))}
            </div>
          )}

          {ajout ? (
            <form onSubmit={handleAjouter} style={{ display: 'flex', gap: 8, marginTop: 12 }}>
              <input type="text" value={nom} onChange={e => setNom(e.target.value)} required placeholder="Nom de la compétence"
                style={{ flex: 1, background: '#1e1509', border: '1px solid #4a3a1a', color: '#d4c4a0', padding: '7px 10px', borderRadius: 5, fontFamily: crimson, fontSize: 13, outline: 'none' }} />
              <button type="submit" style={{ background: '#4a7030', border: '1px solid #6a9040', color: '#c8e0a0', padding: '7px 12px', borderRadius: 5, fontFamily: cinzel, fontSize: 11, cursor: 'pointer' }}>OK</button>
              <button type="button" onClick={() => setAjout(false)} style={{ background: 'transparent', border: '1px solid #4a3a1a', color: '#8a7a5a', padding: '7px 12px', borderRadius: 5, fontFamily: cinzel, fontSize: 11, cursor: 'pointer' }}>✕</button>
            </form>
          ) : (
            <button onClick={() => setAjout(true)} style={{ width: '100%', marginTop: 12, background: 'transparent', border: '1px solid #3a2c18', color: '#6a5a3a', padding: '8px', borderRadius: 5, fontFamily: cinzel, fontSize: 11, cursor: 'pointer', letterSpacing: '0.04em' }}>
              + Add Skill
            </button>
          )}
        </div>
      )}
    </div>
  )
}