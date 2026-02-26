import { useState, useEffect } from 'react'
import * as api from '../../api/api'

export default function ListeFiches({ onSelectFiche, onCreerFiche }) {
  const [fiches, setFiches] = useState([])
  const [chargement, setChargement] = useState(true)
  const [erreur, setErreur] = useState('')

  const chargerFiches = async () => {
    setChargement(true)
    setErreur('')
    try {
      const data = await api.listerFiches()
      setFiches(Array.isArray(data) ? data : [])
    } catch (err) {
      setErreur(err.message)
    } finally {
      setChargement(false)
    }
  }

  useEffect(() => { chargerFiches() }, [])

  const handleSupprimer = async (id) => {
    if (!confirm('Supprimer cette fiche ?')) return
    try {
      await api.supprimerFiche(id)
      chargerFiches()
    } catch (err) {
      setErreur(err.message)
    }
  }

  if (chargement) {
    return <p className="text-gray-400 text-center">Chargement...</p>
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-white">Mes Fiches de Personnage</h2>
        <button
          onClick={onCreerFiche}
          className="bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 px-4 rounded-lg transition"
        >
          + Nouvelle Fiche
        </button>
      </div>

      {erreur && (
        <div className="bg-red-900/50 border border-red-700 text-red-400 text-sm p-3 rounded-lg mb-4">
          {erreur}
        </div>
      )}

      {fiches.length === 0 ? (
        <div className="text-center py-16">
          <p className="text-gray-400 text-lg mb-4">Aucune fiche de personnage</p>
          <button
            onClick={onCreerFiche}
            className="bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 px-4 rounded-lg transition"
          >
            Creer ma premiere fiche
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {fiches.map((fiche) => (
            <div
              key={fiche.id}
              className="bg-gray-800 rounded-xl p-5 border border-gray-700 hover:border-indigo-500 transition cursor-pointer"
              onClick={() => onSelectFiche(fiche.id)}
            >
              <div className="flex items-center justify-between">
                <h3 className="text-lg font-semibold text-white">{fiche.nom}</h3>
                <button
                  onClick={(e) => { e.stopPropagation(); handleSupprimer(fiche.id) }}
                  className="text-red-400 hover:text-red-300 text-sm transition"
                >
                  Supprimer
                </button>
              </div>
              <p className="text-gray-400 text-sm mt-1">ID: {fiche.id}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
