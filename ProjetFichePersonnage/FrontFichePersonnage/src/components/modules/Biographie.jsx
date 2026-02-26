import { useState } from 'react'
import * as api from '../../api/api'

export default function Biographie({ biographie, idFiche, onUpdate }) {
  const [edition, setEdition] = useState(false)
  const [texte, setTexte] = useState(biographie.texte || '')
  const [erreur, setErreur] = useState('')

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

  return (
    <div className="bg-gray-800 rounded-xl p-5 border border-gray-700">
      <div className="flex items-center justify-between mb-3">
        <h3 className="text-lg font-semibold text-white">Biographie</h3>
        <button
          onClick={() => setEdition(!edition)}
          className="text-indigo-400 hover:text-indigo-300 text-sm"
        >
          {edition ? 'Annuler' : 'Modifier'}
        </button>
      </div>

      {erreur && (
        <div className="bg-red-900/50 border border-red-700 text-red-400 text-sm p-2 rounded mb-3">
          {erreur}
        </div>
      )}

      {edition ? (
        <div>
          <textarea
            value={texte}
            onChange={(e) => setTexte(e.target.value)}
            rows={5}
            className="w-full bg-gray-700 border border-gray-600 rounded-lg py-2 px-3 text-white text-sm placeholder-gray-500 focus:outline-none focus:border-indigo-500 mb-2 resize-none"
            placeholder="Ecrivez la biographie du personnage..."
          />
          <button onClick={handleSave} className="bg-indigo-600 hover:bg-indigo-700 text-white text-sm py-2 px-4 rounded-lg transition">
            Sauvegarder
          </button>
        </div>
      ) : (
        <p className="text-gray-300 text-sm whitespace-pre-wrap">
          {biographie.texte || 'Aucune biographie'}
        </p>
      )}
    </div>
  )
}
