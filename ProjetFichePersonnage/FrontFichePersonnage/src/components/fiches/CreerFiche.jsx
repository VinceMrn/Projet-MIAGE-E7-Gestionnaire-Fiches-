import { useState } from 'react'
import * as api from '../../api/api'

export default function CreerFiche({ onRetour, onFicheCree }) {
  const [nom, setNom] = useState('')
  const [erreur, setErreur] = useState('')

  const cinzel = "'Cinzel', serif"
  const crimson = "'Crimson Text', Georgia, serif"

  const handleSubmit = async (e) => {
    e.preventDefault()
    setErreur('')
    try {
      const data = await api.creerFiche(nom)
      onFicheCree(data.id)
    } catch (err) {
      setErreur(err.message)
    }
  }

  return (
    <div style={{ fontFamily: crimson, color: '#d4c4a0' }}>

      <button onClick={onRetour} style={{ background: 'transparent', border: 'none', color: '#a09070', fontFamily: cinzel, fontSize: 12, cursor: 'pointer', letterSpacing: '0.04em', marginBottom: 24 }}>
        ← Retour aux fiches
      </button>

      <div style={{ maxWidth: 500, margin: '0 auto' }}>
        <div style={{ background: '#2a1f14', border: '1px solid #5c4a2a', borderRadius: 10, padding: 32 }}>

          <h2 style={{ fontFamily: cinzel, fontSize: 20, fontWeight: 600, color: '#e8d5a0', margin: '0 0 6px', letterSpacing: '0.06em', textTransform: 'uppercase' }}>
            Nouveau Personnage
          </h2>
          <p style={{ fontSize: 13, color: '#8a7a5a', fontStyle: 'italic', margin: '0 0 24px' }}>
            Crée un personnage et commence ton histoire.
          </p>

          <div style={{ height: 1, background: '#3a2c18', marginBottom: 24 }} />

          {erreur && (
            <div style={{ background: '#4a1515', border: '1px solid #8a3030', color: '#f0a0a0', padding: '10px 14px', borderRadius: 6, fontSize: 13, marginBottom: 16 }}>
              {erreur}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <label style={{ display: 'block', fontFamily: cinzel, fontSize: 11, color: '#6a5a3a', letterSpacing: '0.08em', textTransform: 'uppercase', marginBottom: 8 }}>
              Nom du personnage
            </label>
            <input
              type="text"
              value={nom}
              onChange={e => setNom(e.target.value)}
              style={{ width: '100%', background: '#1e1509', border: '1px solid #4a3a1a', color: '#d4c4a0', padding: '10px 14px', borderRadius: 6, fontFamily: crimson, fontSize: 14, outline: 'none', boxSizing: 'border-box', marginBottom: 20 }}
              placeholder="Ex: Aragorn, Elara Moonwhisper..."
              required
            />

            <div style={{ display: 'flex', gap: 10 }}>
              <button
                type="submit"
                style={{ flex: 1, background: '#4a7030', border: '1px solid #6a9040', color: '#c8e0a0', padding: '10px', borderRadius: 6, fontFamily: cinzel, fontSize: 12, cursor: 'pointer', fontWeight: 600, letterSpacing: '0.04em' }}
              >
                Créer la fiche
              </button>
              <button
                type="button"
                onClick={onRetour}
                style={{ flex: 1, background: 'transparent', border: '1px solid #4a3a1a', color: '#8a7a5a', padding: '10px', borderRadius: 6, fontFamily: cinzel, fontSize: 12, cursor: 'pointer', letterSpacing: '0.04em' }}
              >
                Annuler
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}
