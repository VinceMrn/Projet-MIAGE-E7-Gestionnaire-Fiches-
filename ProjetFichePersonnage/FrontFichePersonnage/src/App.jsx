import { useState } from 'react'
import { AuthProvider, useAuth } from './context/AuthContext'
import LoginForm from './components/auth/LoginForm'
import SignupForm from './components/auth/SignupForm'
import MotDePasseOublie from './components/auth/MotDePasseOublie'
import ParametresCompte from './components/auth/ParametresCompte'
import Layout from './components/Layout'
import ListeFiches from './components/fiches/ListeFiches'
import CreerFiche from './components/fiches/CreerFiche'
import FicheDetail from './components/fiches/FicheDetail'
import CombatPage from './components/Combat/CombatPage'

function AppContent() {
  const { utilisateur } = useAuth()
  const [page, setPage] = useState('login') // login | signup | fiches | creer | detail
  const [ficheSelectionnee, setFicheSelectionnee] = useState(null)

  // Non connecte → auth
  if (!utilisateur) {
    if (page === 'signup') {
      return (
        <div>
          <SignupForm onSwitchToLogin={() => setPage('login')} />
        </div>
      )
    }
    if (page === 'motdepasseoublie') {
      return (
        <div>
          <MotDePasseOublie onRetour={() => setPage('login')} />
        </div>
      )
    }
    return (
      <div>
        <LoginForm
          onSwitchToSignup={() => setPage('signup')}
          onSwitchToOublie={() => setPage('motdepasseoublie')}
          onSuccess={() => setPage('fiches')}
        />
      </div>
    )
  }

  // Connecte → app
  const renderPage = () => {
    switch (page) {
      case 'creer':
        return (
          <CreerFiche
            onRetour={() => setPage('fiches')}
            onFicheCree={(id) => { setFicheSelectionnee(id); setPage('detail') }}
          />
        )
      case 'detail':
        return (
          <FicheDetail
            idFiche={ficheSelectionnee}
            onRetour={() => setPage('fiches')}
          />
        )
      case 'combat':
        return <CombatPage onRetour={() => setPage('fiches')} />
      case 'parametres':
        return <ParametresCompte onRetour={() => setPage('fiches')} />
      default:
        return (
          <ListeFiches
            onSelectFiche={(id) => { setFicheSelectionnee(id); setPage('detail') }}
            onCreerFiche={() => setPage('creer')}
          />
        )
    }
  }

  return (
    <Layout page={page} onNavigate={setPage}>
      {renderPage()}
    </Layout>
  )
}

export default function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  )
}
