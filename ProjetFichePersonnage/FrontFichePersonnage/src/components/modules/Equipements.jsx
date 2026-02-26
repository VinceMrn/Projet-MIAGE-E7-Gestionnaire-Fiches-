import { useState } from 'react'
import * as api from '../../api/api'

export default function Equipements({ equipements, idFiche, onUpdate }) {
  const [nom, setNom] = useState('')
  const [erreur, setErreur] = useState('')
  const [ajout, setAjout] = useState(false)

  const handleAjouter = async (e) => {
    e.preventDefault()
    setErreur('')
    try {
      await api.ajouterEquipement(idFiche, nom)
      setNom('')
      setAjout(false)
      onUpdate()
    } catch (err) {
      setErreur(err.message)
    }
  }

  return (
    <div className="bg-gray-800 rounded-xl p-5 border border-gray-700">
      <div className="flex items-center justify-between mb-3">
        <h3 className="text-lg font-semibold text-white">Equipements</h3>
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

      {equipements.liste.length === 0 ? (
        <p className="text-gray-500 text-sm">Aucun equipement</p>
      ) : (
        <div className="flex flex-wrap gap-2">
          {equipements.liste.map((equip, i) => (
            <span key={i} className="bg-gray-700 text-gray-300 text-sm px-3 py-1 rounded-full">
              {equip}
            </span>
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
            placeholder="Nom de l'equipement"
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
