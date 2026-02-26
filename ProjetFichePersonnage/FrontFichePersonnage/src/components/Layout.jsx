import { useAuth } from '../context/AuthContext'

export default function Layout({ children, page, onNavigate }) {
  const { utilisateur, seDeconnecter } = useAuth()

  const handleLogout = async () => {
    await seDeconnecter()
    onNavigate('login')
  }

  return (
    <div className="min-h-screen bg-gray-900">
      {/* Navbar */}
      <nav className="bg-gray-800 border-b border-gray-700">
        <div className="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between">
          <button
            onClick={() => onNavigate('fiches')}
            className="text-xl font-bold text-white hover:text-indigo-400 transition"
          >
            Gestionnaire de Fiches
          </button>

          <div className="flex items-center gap-4">
            <button
              onClick={() => onNavigate('fiches')}
              className={`text-sm font-medium transition ${
                page === 'fiches' ? 'text-indigo-400' : 'text-gray-400 hover:text-white'
              }`}
            >
              Mes Fiches
            </button>

            <div className="flex items-center gap-3">
              <div className="w-8 h-8 bg-indigo-600 rounded-full flex items-center justify-center">
                <span className="text-sm text-white font-bold">
                  {utilisateur.nom.charAt(0).toUpperCase()}
                </span>
              </div>
              <span className="text-gray-300 text-sm">{utilisateur.nom}</span>
              <button
                onClick={handleLogout}
                className="text-red-400 hover:text-red-300 text-sm font-medium transition"
              >
                Deconnexion
              </button>
            </div>
          </div>
        </div>
      </nav>

      {/* Contenu */}
      <main className="max-w-6xl mx-auto px-4 py-8">
        {children}
      </main>
    </div>
  )
}
