import { useState } from 'react'
import { useAuth } from '../../context/AuthContext'

export default function SignupForm({ onSwitchToLogin }) {
  const { sInscrire } = useAuth()
  const [nom, setNom] = useState('')
  const [motdepasse, setMotdepasse] = useState('')
  const [message, setMessage] = useState('')
  const [erreur, setErreur] = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    setErreur('')
    setMessage('')
    try {
      const data = await sInscrire(nom, motdepasse)
      setMessage(`Compte cree pour ${data.nom} ! Vous pouvez maintenant vous connecter.`)
      setMotdepasse('')
    } catch (err) {
      setErreur(err.message)
    }
  }

  return (
    <div className="bg-gray-800 p-8 rounded-2xl shadow-xl w-full max-w-md">
      <h1 className="text-2xl font-bold text-white text-center mb-2">
        Gestionnaire de Fiches
      </h1>
      <p className="text-gray-400 text-center mb-6">Creez un nouveau compte</p>

      {message && (
        <div className="bg-green-900/50 border border-green-700 text-green-400 text-sm p-3 rounded-lg mb-4">
          {message}
        </div>
      )}
      {erreur && (
        <div className="bg-red-900/50 border border-red-700 text-red-400 text-sm p-3 rounded-lg mb-4">
          {erreur}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="mb-4">
          <label className="block text-gray-300 text-sm font-medium mb-2">
            Nom d'utilisateur
          </label>
          <input
            type="text"
            value={nom}
            onChange={(e) => setNom(e.target.value)}
            className="w-full bg-gray-700 border border-gray-600 rounded-lg py-3 px-4 text-white placeholder-gray-500 focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
            placeholder="Choisissez un nom"
            required
          />
        </div>
        <div className="mb-6">
          <label className="block text-gray-300 text-sm font-medium mb-2">
            Mot de passe
          </label>
          <input
            type="password"
            value={motdepasse}
            onChange={(e) => setMotdepasse(e.target.value)}
            className="w-full bg-gray-700 border border-gray-600 rounded-lg py-3 px-4 text-white placeholder-gray-500 focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
            placeholder="Choisissez un mot de passe"
            required
          />
        </div>
        <button
          type="submit"
          className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-3 px-4 rounded-lg transition"
        >
          S'inscrire
        </button>
      </form>

      <p className="text-gray-400 text-sm text-center mt-4">
        Deja un compte ?{' '}
        <button onClick={onSwitchToLogin} className="text-indigo-400 hover:text-indigo-300">
          Se connecter
        </button>
      </p>
    </div>
  )
}
