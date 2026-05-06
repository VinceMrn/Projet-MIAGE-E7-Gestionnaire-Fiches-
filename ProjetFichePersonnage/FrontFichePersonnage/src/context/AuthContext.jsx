import { createContext, useContext, useState } from 'react'
import * as api from '../api/api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [utilisateur, setUtilisateur] = useState(null)
  const [sessionId, setSessionId] = useState(null)

  const seConnecter = async (nom, motdepasse) => {
    const data = await api.login(nom, motdepasse)
    setUtilisateur({ id: data.id, nom: data.nom })
    setSessionId(data.sessionId)
    localStorage.setItem('sessionId', data.sessionId)
    return data
  }

  const sInscrire = async (nom, motdepasse) => {
    return await api.signup(nom, motdepasse)
  }

  const seDeconnecter = async () => {
    await api.logout()
    setUtilisateur(null)
    setSessionId(null)
    localStorage.removeItem('sessionId')
  }

  return (
    <AuthContext.Provider value={{ utilisateur, sessionId, seConnecter, sInscrire, seDeconnecter }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth doit etre utilise dans un AuthProvider')
  return ctx
}
