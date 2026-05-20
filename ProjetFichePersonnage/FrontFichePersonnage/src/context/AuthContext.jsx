import { createContext, useContext, useState } from 'react'
import * as api from '../api/api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [utilisateur, setUtilisateur] = useState(null)

  const seConnecter = async (nom, motdepasse) => {
    const data = await api.login(nom, motdepasse)
    setUtilisateur({ id: data.id, nom: data.nom })
    localStorage.setItem('sessionId', data.sessionId) // Stockage du sessionId dans le localStorage
    return data
  }

  const sInscrire = async (nom, motdepasse) => {
    const data = await api.signup(nom, motdepasse)
    setUtilisateur({ id: data.id, nom: data.nom })
    localStorage.setItem('sessionId', data.sessionId) // Stockage du sessionId dans le localStorage
    return data;
  }

  const seDeconnecter = async () => {
    await api.logout()
    setUtilisateur(null)
    localStorage.removeItem('sessionId') // Suppression du sessionId du localStorage
  }

  const modifierIdentifiant = async (nouveauNom) => {
    const data = await api.modifierIdentifiant(nouveauNom)
    setUtilisateur({ id: data.id, nom: data.nom })
    return data
  }

  const modifierMotDePasse = async (ancien, nouveau) => {
    return await api.modifierMotDePasse(ancien, nouveau)
  }

  // Question secrete : utilisee depuis ParametresCompte (user connecte)
  const definirQuestionSecrete = async (question, reponse) => {
    return await api.definirQuestionSecrete(question, reponse)
  }

  // Recuperation : utilisees depuis le flow "mot de passe oublie" (user NON connecte)
  const getQuestionSecrete = async (nom) => {
    return await api.getQuestionSecrete(nom)
  }

  const reinitialiserMotDePasse = async (nom, reponse, nouveau) => {
    return await api.reinitialiserMotDePasse(nom, reponse, nouveau)
  }

  return (
    <AuthContext.Provider value={{
      utilisateur,
      seConnecter, sInscrire, seDeconnecter,
      modifierIdentifiant, modifierMotDePasse,
      definirQuestionSecrete, getQuestionSecrete, reinitialiserMotDePasse
    }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth doit etre utilise dans un AuthProvider')
  return ctx
}
