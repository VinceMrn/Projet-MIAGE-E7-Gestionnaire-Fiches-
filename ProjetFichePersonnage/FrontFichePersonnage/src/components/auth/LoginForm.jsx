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
    <div className="min-h-screen w-full bg-medieval flex items-center justify-center">
      <div className="card-medieval rounded-xl p-10 w-full max-w-md animate-fade-in-up">

        <div className="flex items-center justify-center mb-2">
          <span className="text-gold opacity-40 text-2xl tracking-[0.5em] font-medieval">⚔</span>
        </div>

        <h1 className="font-medieval text-3xl font-bold text-center mb-1 gold-shimmer">
          Grimoire des Héros
        </h1>
        <p className="font-body text-parchment-dark text-center text-sm italic mb-2">
          Gestionnaire de Fiches de Personnages
        </p>

        <div className="divider-medieval my-6" />

        <p className="font-body text-parchment-dark text-center mb-6 text-[0.95rem]">
          Entrez vos identifiants, aventurier
        </p>

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
              placeholder="Votre nom..."
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
            Entrer dans le Royaume
          </button>
        </form>

        <div className="divider-medieval my-6" />

        <p className="font-body text-parchment-dark text-sm text-center">
          Nouveau dans ces contrées ?{' '}
          <button
            onClick={onSwitchToSignup}
            className="text-gold hover:text-gold-light transition font-semibold"
          >
            Rejoindre la Guilde
          </button>
        </p>

        <div className="flex items-center justify-center mt-4">
          <span className="text-gold opacity-20 text-xs tracking-[0.3em] font-medieval">◆ ◆ ◆</span>
        </div>
      </div>
    </div>
  )
}
