import { useState } from 'react'
import * as api from '../../api/api'

export default function Statistiques({ statistiques, idFiche, onUpdate }) {
  const [nom, setNom] = useState('')
  const [valeur, setValeur] = useState('')
  const [erreur, setErreur] = useState('')
  const [ajout, setAjout] = useState(false)

  const handleAjouter = async (e) => {
    e.preventDefault()
    setErreur('')
    try {
      await api.ajouterStatistique(idFiche, nom, parseInt(valeur))
      setNom('')
      setValeur('')
      setAjout(false)
      onUpdate()
    } catch (err) {
      setErreur(err.message)
    }
  }

  return (
    <div className="bg-gray-800 rounded-xl p-5 border border-gray-700">
      <div className="flex items-center justify-between mb-3">
        <h3 className="text-lg font-semibold text-white">Statistiques</h3>
        <button
          onClick={() => setAjout(!ajout)}
          className="text-indigo-400 hover:text-indigo-300 text-sm"
        >
          {ajout ? 'Annuler' : '+ Ajouter'}
        </button>
      </div>

      {erreur && (
        <div className="bg-red-900/50 border border-red-700 text-red-400 text-sm p-2 rounded mb-3">
          {erreur}
        </div>
      )}

      {statistiques.liste.length === 0 ? (
        <p className="text-gray-500 text-sm">Aucune statistique</p>
      ) : (
        <div className="space-y-2">
          {statistiques.liste.map((stat) => (
            <div key={stat.id} className="flex items-center justify-between bg-gray-700 rounded-lg px-3 py-2">
              <span className="text-gray-300 text-sm">{stat.nom}</span>
              <span className="text-indigo-400 font-semibold">{stat.valeur}</span>
            </div>
          ))}
        </div>
      )}

      {ajout && (
        <form onSubmit={handleAjouter} className="mt-3 flex gap-2">
          <input
            type="text"
            value={nom}
            onChange={(e) => setNom(e.target.value)}
            className="flex-1 bg-gray-700 border border-gray-600 rounded-lg py-2 px-3 text-white text-sm placeholder-gray-500 focus:outline-none focus:border-indigo-500"
            placeholder="Nom (ex: Force)"
            required
          />
          <input
            type="number"
            value={valeur}
            onChange={(e) => setValeur(e.target.value)}
            className="w-20 bg-gray-700 border border-gray-600 rounded-lg py-2 px-3 text-white text-sm placeholder-gray-500 focus:outline-none focus:border-indigo-500"
            placeholder="Val"
            required
          />
          <button type="submit" className="bg-indigo-600 hover:bg-indigo-700 text-white text-sm py-2 px-3 rounded-lg transition">
            OK
          </button>
        </form>
      )}
    </div>
  )
}
