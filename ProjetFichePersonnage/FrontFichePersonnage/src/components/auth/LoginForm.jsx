import { useState } from 'react'
import { useAuth } from '../../context/AuthContext'

export default function LoginForm({ onSwitchToSignup, onSuccess }) {
  const { seConnecter } = useAuth()
  const [nom, setNom] = useState('')
  const [motdepasse, setMotdepasse] = useState('')
  const [erreur, setErreur] = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    setErreur('')
    try {
      await seConnecter(nom, motdepasse)
      onSuccess?.()
    } catch (err) {
      setErreur(err.message)
    }
  }

  return (
    <div className="bg-gray-800 p-8 rounded-2xl shadow-xl w-full max-w-md">
      <h1 className="text-2xl font-bold text-white text-center mb-2">
        Gestionnaire de Fiches
      </h1>
      <p className="text-gray-400 text-center mb-6">Connectez-vous a votre compte</p>

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
            placeholder="Entrez votre nom"
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
            placeholder="Entrez votre mot de passe"
            required
          />
        </div>
        <button
          type="submit"
          className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-3 px-4 rounded-lg transition"
        >
          Se connecter
        </button>
      </form>

      <p className="text-gray-400 text-sm text-center mt-4">
        Pas de compte ?{' '}
        <button onClick={onSwitchToSignup} className="text-indigo-400 hover:text-indigo-300">
          S'inscrire
        </button>
      </p>
    </div>
  )
}
