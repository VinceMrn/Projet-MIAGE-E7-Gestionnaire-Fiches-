import { useState } from 'react'

/**
 * Affiche les elements deja crees (dans d'autres fiches de l'utilisateur)
 * sous forme de puces cliquables pour les ajouter a la fiche courante.
 *
 * Props :
 * - items : array — soit strings (compétences/équipements), soit { nom, valeur } (stats)
 * - dejaPresents : array de noms à filtrer (déjà sur la fiche courante)
 * - onChoisir : (item) => void — callback quand l'utilisateur clique sur une puce
 * - label : string — utilisé dans le bouton de bascule (ex: "compétence")
 */
export default function SelecteurExistants({ items = [], dejaPresents = [], onChoisir, label }) {
  const [ouvert, setOuvert] = useState(false)

  const cinzel = "'Cinzel', serif"
  const crimson = "'Crimson Text', Georgia, serif"

  const disponibles = items.filter(i => {
    const nom = typeof i === 'string' ? i : i.nom
    return !dejaPresents.includes(nom)
  })

  return (
    <div style={{ marginTop: 10 }}>
      <button
        type="button"
        onClick={() => setOuvert(!ouvert)}
        style={{
          width: '100%',
          background: 'transparent',
          border: '1px dashed #3a2c18',
          color: '#8a7a5a',
          padding: '7px',
          borderRadius: 5,
          fontFamily: cinzel,
          fontSize: 11,
          cursor: 'pointer',
          letterSpacing: '0.04em',
          textTransform: 'uppercase',
        }}
      >
        {ouvert ? '∧' : '∨'} Réutiliser un{label?.endsWith('e') ? 'e' : ''} {label} ({disponibles.length})
      </button>

      {ouvert && (
        <div style={{
          display: 'flex',
          flexWrap: 'wrap',
          gap: 6,
          marginTop: 8,
          padding: 10,
          background: '#1e1509',
          border: '1px solid #3a2c18',
          borderRadius: 5,
        }}>
          {disponibles.length === 0 && (
            <span style={{ color: '#5a4a2a', fontStyle: 'italic', fontSize: 12, fontFamily: crimson }}>
              Aucun{label?.endsWith('e') ? 'e' : ''} {label} à réutiliser depuis tes autres fiches.
            </span>
          )}
          {disponibles.map((item, i) => {
            const nom = typeof item === 'string' ? item : item.nom
            const valeur = typeof item === 'object' ? item.valeur : undefined
            return (
              <button
                key={i}
                type="button"
                onClick={() => onChoisir(item)}
                style={{
                  background: '#2a1f14',
                  border: '1px solid #4a3a1a',
                  color: '#c4b080',
                  padding: '5px 12px',
                  borderRadius: 14,
                  cursor: 'pointer',
                  fontFamily: crimson,
                  fontSize: 12,
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: 6,
                }}
                onMouseEnter={e => { e.currentTarget.style.background = '#3a2c18'; e.currentTarget.style.borderColor = '#6a5a3a' }}
                onMouseLeave={e => { e.currentTarget.style.background = '#2a1f14'; e.currentTarget.style.borderColor = '#4a3a1a' }}
              >
                + {nom}
                {valeur !== undefined && (
                  <span style={{ color: '#c4a86a', fontWeight: 600 }}>({valeur})</span>
                )}
              </button>
            )
          })}
        </div>
      )}
    </div>
  )
}
