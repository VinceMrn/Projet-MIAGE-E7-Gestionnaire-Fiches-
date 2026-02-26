import { useState } from 'react'
import * as api from '../../api/api'

export default function Portrait({ portrait, idFiche, onUpdate }) {
  const [edition, setEdition] = useState(false)
  const [image, setImage] = useState(portrait.image || '')
  const [erreur, setErreur] = useState('')

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

  return (
    <div className="bg-gray-800 rounded-xl p-5 border border-gray-700">
      <div className="flex items-center justify-between mb-3">
        <h3 className="text-lg font-semibold text-white">Portrait</h3>
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

      {portrait.image ? (
        <div className="w-32 h-32 bg-gray-700 rounded-lg flex items-center justify-center mb-3 overflow-hidden">
          <img src={portrait.image} alt="Portrait" className="w-full h-full object-cover"
            onError={(e) => { e.target.style.display = 'none' }} />
        </div>
      ) : (
        <div className="w-32 h-32 bg-gray-700 rounded-lg flex items-center justify-center mb-3">
          <span className="text-gray-500 text-sm">Pas d'image</span>
        </div>
      )}

      {edition && (
        <div className="mt-3">
          <input
            type="text"
            value={image}
            onChange={(e) => setImage(e.target.value)}
            className="w-full bg-gray-700 border border-gray-600 rounded-lg py-2 px-3 text-white text-sm placeholder-gray-500 focus:outline-none focus:border-indigo-500 mb-2"
            placeholder="URL de l'image"
          />
          <button onClick={handleSave} className="bg-indigo-600 hover:bg-indigo-700 text-white text-sm py-2 px-4 rounded-lg transition">
            Sauvegarder
          </button>
        </div>
      )}
    </div>
  )
}
