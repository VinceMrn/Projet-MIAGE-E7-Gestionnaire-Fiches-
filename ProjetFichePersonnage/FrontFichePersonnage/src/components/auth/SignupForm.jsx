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
      setMessage(`Bienvenue ${data.nom} ! Votre nom est inscrit dans les registres.`)
      setMotdepasse('')
    } catch (err) {
      setErreur(err.message)
    }
  }

  return (
    <div className="min-h-screen w-full bg-medieval flex items-center justify-center">
      <div className="card-medieval rounded-xl p-10 w-full max-w-md animate-fade-in-up">

        <div className="flex items-center justify-center mb-2">
          <span className="text-gold opacity-40 text-2xl tracking-[0.5em] font-medieval">🛡</span>
        </div>

        <h1 className="font-medieval text-3xl font-bold text-center mb-1 gold-shimmer">
          Rejoindre la Guilde
        </h1>
        <p className="font-body text-parchment-dark text-center text-sm italic mb-2">
          Inscrivez votre nom dans les chroniques
        </p>

        <div className="divider-medieval my-6" />

        <p className="font-body text-parchment-dark text-center mb-6 text-[0.95rem]">
          Choisissez votre identité, recrue
        </p>

        {message && (
          <div className="bg-moss-dark/40 border border-moss/50 text-green-300 text-sm p-3 rounded-lg mb-5 font-body text-center">
            ✦ {message}
          </div>
        )}

        {erreur && (
          <div className="bg-blood-dark/40 border border-blood/50 text-red-300 text-sm p-3 rounded-lg mb-5 font-body text-center">
            ⚠ {erreur}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="mb-5">
            <label className="block text-gold/70 text-xs font-medieval uppercase tracking-widest mb-2">
              Nom d'aventurier
            </label>
            <input
              type="text"
              value={nom}
              onChange={(e) => setNom(e.target.value)}
              className="w-full input-medieval rounded-lg py-3 px-4"
              placeholder="Choisissez un nom..."
              required
            />
          </div>

          <div className="mb-7">
            <label className="block text-gold/70 text-xs font-medieval uppercase tracking-widest mb-2">
              Mot de passe
            </label>
            <input
              type="password"
              value={motdepasse}
              onChange={(e) => setMotdepasse(e.target.value)}
              className="w-full input-medieval rounded-lg py-3 px-4"
              placeholder="Votre secret..."
              required
            />
          </div>

          <button type="submit" className="w-full btn-medieval py-3 px-4 rounded-lg">
            Forger mon Destin
          </button>
        </form>

        <div className="divider-medieval my-6" />

        <p className="font-body text-parchment-dark text-sm text-center">
          Déjà membre de la Guilde ?{' '}
          <button
            onClick={onSwitchToLogin}
            className="text-gold hover:text-gold-light transition font-semibold"
          >
            Se connecter
          </button>
        </p>

        <div className="flex items-center justify-center mt-4">
          <span className="text-gold opacity-20 text-xs tracking-[0.3em] font-medieval">◆ ◆ ◆</span>
        </div>
      </div>
    </div>
  )
}
