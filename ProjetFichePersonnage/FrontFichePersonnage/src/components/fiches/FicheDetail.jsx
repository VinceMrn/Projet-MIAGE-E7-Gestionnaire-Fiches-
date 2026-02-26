import { useState, useEffect } from 'react'
import * as api from '../../api/api'
import Portrait from '../modules/Portrait'
import Biographie from '../modules/Biographie'
import Statistiques from '../modules/Statistiques'
import Competences from '../modules/Competences'
import Equipements from '../modules/Equipements'

export default function FicheDetail({ idFiche, onRetour }) {
  const [fiche, setFiche] = useState(null)
  const [chargement, setChargement] = useState(true)
  const [erreur, setErreur] = useState('')

  const chargerFiche = async () => {
    setChargement(true)
    setErreur('')
    try {
      const data = await api.getFiche(idFiche)
      setFiche(data)
    } catch (err) {
      setErreur(err.message)
    } finally {
      setChargement(false)
    }
  }

  useEffect(() => { chargerFiche() }, [idFiche])

  if (chargement) return <p className="text-gray-400 text-center">Chargement...</p>

  if (erreur) {
    return (
      <div>
        <button onClick={onRetour} className="text-indigo-400 hover:text-indigo-300 mb-4">
          &larr; Retour
        </button>
        <div className="bg-red-900/50 border border-red-700 text-red-400 p-3 rounded-lg">
          {erreur}
        </div>
      </div>
    )
  }

  return (
    <div>
      <button onClick={onRetour} className="text-indigo-400 hover:text-indigo-300 mb-6 inline-block">
        &larr; Retour aux fiches
      </button>

      <h2 className="text-3xl font-bold text-white mb-8">{fiche.nom}</h2>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Portrait portrait={fiche.portrait} idFiche={idFiche} onUpdate={chargerFiche} />
        <Biographie biographie={fiche.biographie} idFiche={idFiche} onUpdate={chargerFiche} />
        <Statistiques statistiques={fiche.statistiques} idFiche={idFiche} onUpdate={chargerFiche} />
        <Competences competences={fiche.competences} idFiche={idFiche} onUpdate={chargerFiche} />
        <Equipements equipements={fiche.equipements} idFiche={idFiche} onUpdate={chargerFiche} />
      </div>
    </div>
  )
}
