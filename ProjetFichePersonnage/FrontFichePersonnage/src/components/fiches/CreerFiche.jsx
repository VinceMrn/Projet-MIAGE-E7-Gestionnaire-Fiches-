import { useState } from 'react'
import * as api from '../../api/api'

export default function CreerFiche({ onRetour, onFicheCree }) {
  const [nom, setNom] = useState('')
  const [erreur, setErreur] = useState('')

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
    <div>
      <button onClick={onRetour} className="text-indigo-400 hover:text-indigo-300 mb-6 inline-block">
        &larr; Retour aux fiches
      </button>

      <div className="bg-gray-800 p-8 rounded-2xl shadow-xl max-w-md mx-auto">
        <h2 className="text-2xl font-bold text-white text-center mb-6">
          Nouvelle Fiche de Personnage
        </h2>

        {erreur && (
          <div className="bg-red-900/50 border border-red-700 text-red-400 text-sm p-3 rounded-lg mb-4">
            {erreur}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="mb-6">
            <label className="block text-gray-300 text-sm font-medium mb-2">
              Nom du personnage
            </label>
            <input
              type="text"
              value={nom}
              onChange={(e) => setNom(e.target.value)}
              className="w-full bg-gray-700 border border-gray-600 rounded-lg py-3 px-4 text-white placeholder-gray-500 focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
              placeholder="Ex: Gandalf, Aragorn..."
              required
            />
          </div>
          <button
            type="submit"
            className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-3 px-4 rounded-lg transition"
          >
            Creer la fiche
          </button>
        </form>
      </div>
    </div>
  )
}
