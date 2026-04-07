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
      <div className="flex items-center justify-between mb-6">
        <button onClick={onRetour} className="text-[#d6c8b6] hover:text-white inline-block">
          &larr; Retour aux fiches
        </button>
      </div>

      <div className="max-w-3xl mx-auto">
        <div className="bg-[#2f1f18] border border-[#5b3f2f] rounded-2xl p-8 shadow-2xl">
          <header className="flex items-center justify-between mb-6">
            <div>
              <h2 className="text-3xl font-extrabold text-[#f1d9c2]">Nouveau personnage</h2>
              <p className="text-sm text-[#dbc8b3] mt-1">Crée un personnage et commence ton histoire.</p>
            </div>
            <div>
              <button className="bg-green-600 hover:bg-green-700 text-white font-semibold py-2 px-4 rounded-lg">+ Nouveau Personnage</button>
            </div>
          </header>

          {erreur && (
            <div className="bg-[#4b2110] border border-[#6b2f18] text-[#ffb4a2] text-sm p-3 rounded-lg mb-4">
              {erreur}
            </div>
          )}

          <form onSubmit={handleSubmit} className="grid grid-cols-1 gap-6">
            <div>
              <label className="block text-[#e6d9c8] text-sm font-medium mb-2">Nom du personnage</label>
              <input
                type="text"
                value={nom}
                onChange={(e) => setNom(e.target.value)}
                className="w-full bg-[#3b2b23] border border-[#5b3f2f] rounded-lg py-3 px-4 text-[#f7efe6] placeholder-[#bda78e] focus:outline-none focus:ring-2 focus:ring-[#8b6b4f]"
                placeholder="Ex: Elara Moonwhisper"
                required
              />
            </div>

            <div className="flex gap-4">
              <button
                type="submit"
                className="flex-1 bg-green-600 hover:bg-green-700 text-white font-semibold py-3 rounded-lg transition"
              >
                Créer la fiche
              </button>
              <button type="button" onClick={onRetour} className="flex-1 bg-transparent border border-[#5b3f2f] text-[#e6d9c8] py-3 rounded-lg">Annuler</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}
