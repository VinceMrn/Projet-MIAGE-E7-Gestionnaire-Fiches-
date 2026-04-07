import { useState } from 'react'
import * as api from '../../api/api'

export default function Portrait({ portrait, idFiche, onUpdate }) {
  const [edition, setEdition] = useState(false)
  const [image, setImage] = useState(portrait.image || '')
  const [erreur, setErreur] = useState('')
  const [collapsed, setCollapsed] = useState(false)

  const handleSave = async () => {
    setErreur('')
    try {
      await api.modifierPortrait(idFiche, image)
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
      {/* Header module */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '12px 16px', borderBottom: collapsed ? 'none' : '1px solid #3a2c18' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, paddingLeft: 36 }}>
          <h3 style={{ fontFamily: cinzel, fontSize: 13, fontWeight: 600, color: '#e8d5a0', margin: 0, letterSpacing: '0.05em', textTransform: 'uppercase' }}>Portrait</h3>
        </div>
        <div style={{ display: 'flex', gap: 10 }}>
          <button onClick={() => setCollapsed(!collapsed)} style={{ background: 'transparent', border: 'none', color: '#6a5a3a', cursor: 'pointer', fontSize: 14 }}>{collapsed ? '∨' : '∧'}</button>
          <button onClick={() => setEdition(!edition)} style={{ background: 'transparent', border: 'none', color: '#6a5a3a', cursor: 'pointer', fontSize: 16 }}>⋮</button>
        </div>
      </div>

      {!collapsed && (
        <div style={{ padding: '16px' }}>
          {erreur && <div style={{ background: '#4a1515', border: '1px solid #8a3030', color: '#f0a0a0', padding: '8px 12px', borderRadius: 5, fontSize: 12, marginBottom: 12 }}>{erreur}</div>}

          {portrait.image ? (
            <div style={{ width: 128, height: 128, borderRadius: 8, overflow: 'hidden', border: '1px solid #5c4a2a', marginBottom: 12 }}>
              <img src={portrait.image} alt="Portrait" style={{ width: '100%', height: '100%', objectFit: 'cover' }} onError={e => e.target.style.display = 'none'} />
            </div>
          ) : (
            <div style={{ border: '1px dashed #5c4a2a', borderRadius: 8, padding: '40px 20px', textAlign: 'center', marginBottom: 12 }}>
              <div style={{ fontSize: 32, color: '#5c4a2a', marginBottom: 8 }}>👤</div>
              <div style={{ fontSize: 12, color: '#6a5a3a', marginBottom: 4 }}>Drag & drop une image</div>
              <div style={{ fontSize: 11, color: '#4a3a1a' }}>ou</div>
              <button onClick={() => setEdition(true)} style={{ marginTop: 8, background: 'transparent', border: '1px solid #5c4a2a', color: '#a09070', padding: '5px 14px', borderRadius: 4, fontFamily: cinzel, fontSize: 11, cursor: 'pointer' }}>↑ Choose File</button>
            </div>
          )}

          {edition && (
            <div style={{ marginTop: 8 }}>
              <input
                type="text"
                value={image}
                onChange={e => setImage(e.target.value)}
                style={{ width: '100%', background: '#1e1509', border: '1px solid #4a3a1a', color: '#d4c4a0', padding: '8px 12px', borderRadius: 5, fontFamily: crimson, fontSize: 13, outline: 'none', boxSizing: 'border-box', marginBottom: 8 }}
                placeholder="URL de l'image"
              />
              <div style={{ display: 'flex', gap: 8 }}>
                <button onClick={handleSave} style={{ background: '#4a7030', border: '1px solid #6a9040', color: '#c8e0a0', padding: '6px 14px', borderRadius: 5, fontFamily: cinzel, fontSize: 11, cursor: 'pointer' }}>Sauvegarder</button>
                <button onClick={() => setEdition(false)} style={{ background: 'transparent', border: '1px solid #4a3a1a', color: '#8a7a5a', padding: '6px 14px', borderRadius: 5, fontFamily: cinzel, fontSize: 11, cursor: 'pointer' }}>Annuler</button>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  )
}