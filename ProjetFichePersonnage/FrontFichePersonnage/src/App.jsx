import { useState } from 'react'

const API_URL = 'http://localhost:8080/api'

function App() {
  const [mode, setMode] = useState('login') // 'login' ou 'signup'
  const [nom, setNom] = useState('')
  const [motdepasse, setMotdepasse] = useState('')
  const [utilisateur, setUtilisateur] = useState(null)
  const [message, setMessage] = useState('')
  const [erreur, setErreur] = useState('')

  const resetMessages = () => {
    setMessage('')
    setErreur('')
  }

  const handleLogin = async (e) => {
    e.preventDefault()
    resetMessages()

    try {
      const res = await fetch(`${API_URL}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nom, motdepasse })
      })
      const data = await res.json()

      if (res.ok) {
        setUtilisateur({ id: data.id, nom: data.nom })
        setMessage(`Bienvenue ${data.nom} !`)
        setNom('')
        setMotdepasse('')
      } else {
        setErreur(data.erreur)
      }
    } catch {
      setErreur('Impossible de contacter le serveur. Verifiez que le backend Java tourne.')
    }
  }

  const handleSignup = async (e) => {
    e.preventDefault()
    resetMessages()

    try {
      const res = await fetch(`${API_URL}/signup`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nom, motdepasse })
      })
      const data = await res.json()

      if (res.ok) {
        setMessage(`Compte cree pour ${data.nom} ! Vous pouvez maintenant vous connecter.`)
        setMode('login')
        setMotdepasse('')
      } else {
        setErreur(data.erreur)
      }
    } catch {
      setErreur('Impossible de contacter le serveur. Verifiez que le backend Java tourne.')
    }
  }

  const handleLogout = async () => {
    resetMessages()

    try {
      await fetch(`${API_URL}/logout`, { method: 'POST' })
      setUtilisateur(null)
      setMessage('Deconnexion reussie.')
    } catch {
      setErreur('Erreur lors de la deconnexion.')
    }
  }

  // Ecran quand l'utilisateur est connecte
  if (utilisateur) {
    return (
      <div className="min-h-screen bg-gray-900 flex items-center justify-center">
        <div className="bg-gray-800 p-8 rounded-2xl shadow-xl w-full max-w-md text-center">
          <div className="w-20 h-20 bg-indigo-600 rounded-full flex items-center justify-center mx-auto mb-4">
            <span className="text-3xl text-white font-bold">
              {utilisateur.nom.charAt(0).toUpperCase()}
            </span>
          </div>
          <h1 className="text-2xl font-bold text-white mb-2">
            Bienvenue, {utilisateur.nom}
          </h1>
          <p className="text-gray-400 mb-6">ID: {utilisateur.id}</p>

          {message && (
            <p className="text-green-400 text-sm mb-4">{message}</p>
          )}

          <button
            onClick={handleLogout}
            className="w-full bg-red-600 hover:bg-red-700 text-white font-semibold py-3 px-4 rounded-lg transition"
          >
            Se deconnecter
          </button>
        </div>
      </div>
    )
  }

  // Ecran login / signup
  return (
    <div className="min-h-screen bg-gray-900 flex items-center justify-center">
      <div className="bg-gray-800 p-8 rounded-2xl shadow-xl w-full max-w-md">
        <h1 className="text-2xl font-bold text-white text-center mb-2">
          Gestionnaire de Fiches
        </h1>
        <p className="text-gray-400 text-center mb-6">
          {mode === 'login' ? 'Connectez-vous a votre compte' : 'Creez un nouveau compte'}
        </p>

        {/* Onglets Login / Signup */}
        <div className="flex mb-6 bg-gray-700 rounded-lg p-1">
          <button
            onClick={() => { setMode('login'); resetMessages() }}
            className={`flex-1 py-2 rounded-md text-sm font-medium transition ${
              mode === 'login'
                ? 'bg-indigo-600 text-white'
                : 'text-gray-400 hover:text-white'
            }`}
          >
            Connexion
          </button>
          <button
            onClick={() => { setMode('signup'); resetMessages() }}
            className={`flex-1 py-2 rounded-md text-sm font-medium transition ${
              mode === 'signup'
                ? 'bg-indigo-600 text-white'
                : 'text-gray-400 hover:text-white'
            }`}
          >
            Inscription
          </button>
        </div>

        {/* Messages */}
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

        {/* Formulaire */}
        <form onSubmit={mode === 'login' ? handleLogin : handleSignup}>
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
            {mode === 'login' ? 'Se connecter' : "S'inscrire"}
          </button>
        </form>
      </div>
    </div>
  )
}

export default App
