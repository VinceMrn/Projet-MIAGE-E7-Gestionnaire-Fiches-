import { useState } from 'react'
import * as api from '../../api/api'

export default function Biographie({ biographie, idFiche, onUpdate }) {
  const [edition, setEdition] = useState(false)
  const [texte, setTexte] = useState(biographie.texte || '')
  const [erreur, setErreur] = useState('')
  const [collapsed, setCollapsed] = useState(false)

  const handleSave = async () => {
    setErreur('')
    try {
      await api.modifierBiographie(idFiche, texte)
      setEdition(false)
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
          <h3 style={{ fontFamily: cinzel, fontSize: 13, fontWeight: 600, color: '#e8d5a0', margin: 0, letterSpacing: '0.05em', textTransform: 'uppercase' }}>Biography</h3>
        </div>
        <div style={{ display: 'flex', gap: 10 }}>
          <button onClick={() => setCollapsed(!collapsed)} style={{ background: 'transparent', border: 'none', color: '#6a5a3a', cursor: 'pointer', fontSize: 14 }}>{collapsed ? '∨' : '∧'}</button>
          <button onClick={() => setEdition(!edition)} style={{ background: 'transparent', border: 'none', color: '#6a5a3a', cursor: 'pointer', fontSize: 16 }}>⋮</button>
        </div>
      </div>

      {!collapsed && (
        <div style={{ padding: '16px' }}>
          {erreur && <div style={{ background: '#4a1515', border: '1px solid #8a3030', color: '#f0a0a0', padding: '8px 12px', borderRadius: 5, fontSize: 12, marginBottom: 12 }}>{erreur}</div>}

          {edition ? (
            <div>
              <textarea
                value={texte}
                onChange={e => setTexte(e.target.value)}
                rows={6}
                style={{ width: '100%', background: '#1e1509', border: '1px solid #4a3a1a', color: '#d4c4a0', padding: '8px 12px', borderRadius: 5, fontFamily: crimson, fontSize: 13, outline: 'none', resize: 'none', boxSizing: 'border-box', marginBottom: 8, lineHeight: 1.6 }}
                placeholder="Écrivez la biographie du personnage..."
              />
              <div style={{ display: 'flex', gap: 8 }}>
                <button onClick={handleSave} style={{ background: '#4a7030', border: '1px solid #6a9040', color: '#c8e0a0', padding: '6px 14px', borderRadius: 5, fontFamily: cinzel, fontSize: 11, cursor: 'pointer' }}>Sauvegarder</button>
                <button onClick={() => setEdition(false)} style={{ background: 'transparent', border: '1px solid #4a3a1a', color: '#8a7a5a', padding: '6px 14px', borderRadius: 5, fontFamily: cinzel, fontSize: 11, cursor: 'pointer' }}>Annuler</button>
              </div>
            </div>
          ) : (
            <div>
              <p style={{ fontSize: 14, color: '#c4b490', lineHeight: 1.7, whiteSpace: 'pre-wrap', margin: '0 0 12px' }}>
                {biographie.texte || <span style={{ color: '#5a4a2a', fontStyle: 'italic' }}>Aucune biographie</span>}
              </p>
              <button onClick={() => setEdition(true)} style={{ background: 'transparent', border: '1px solid #4a3a1a', color: '#8a7a5a', padding: '5px 14px', borderRadius: 4, fontFamily: cinzel, fontSize: 11, cursor: 'pointer' }}>✎ Edit Biography</button>
            </div>
          )}
        </div>
      )}
    </div>
  )
}