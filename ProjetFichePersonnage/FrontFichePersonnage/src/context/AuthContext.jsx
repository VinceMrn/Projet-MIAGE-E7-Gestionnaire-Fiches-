import { createContext, useContext, useState } from 'react'
import * as api from '../api/api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [utilisateur, setUtilisateur] = useState(null)

  const seConnecter = async (nom, motdepasse) => {
    const data = await api.login(nom, motdepasse)
    setUtilisateur({ id: data.id, nom: data.nom })
    return data
  }

  const sInscrire = async (nom, motdepasse) => {
    return await api.signup(nom, motdepasse)
  }

  const seDeconnecter = async () => {
    await api.logout()
    setUtilisateur(null)
  }

  const modifierIdentifiant = async (nouveauNom) => {
    const data = await api.modifierIdentifiant(nouveauNom)
    setUtilisateur({ id: data.id, nom: data.nom })
    return data
  }

  const modifierMotDePasse = async (ancien, nouveau) => {
    return await api.modifierMotDePasse(ancien, nouveau)
  }

  return (
    <AuthContext.Provider value={{ utilisateur, seConnecter, sInscrire, seDeconnecter, modifierIdentifiant, modifierMotDePasse }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth doit etre utilise dans un AuthProvider')
  return ctx
}
