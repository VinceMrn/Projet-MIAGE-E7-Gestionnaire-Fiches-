import { useAuth } from '../context/AuthContext'

export default function Layout({ children, page, onNavigate }) {
  const { utilisateur, seDeconnecter } = useAuth()

  const handleLogout = async () => {
    await seDeconnecter()
    onNavigate('login')
  }

  const cinzel = "'Cinzel', serif"
  const crimson = "'Crimson Text', Georgia, serif"

  return (
    <div style={{ fontFamily: crimson, background: '#1a1208', minHeight: '100vh', color: '#d4c4a0', display: 'flex', flexDirection: 'column' }}>
      <style>{`@import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@400;600&family=Crimson+Text:ital,wght@0,400;0,600;1,400&display=swap');`}</style>

      {/* ── Header ── */}
      <header style={{ background: '#110d05', borderBottom: '1px solid #5c4a2a', padding: '10px 28px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        {/* Logo */}
        <div onClick={() => onNavigate('fiches')} style={{ cursor: 'pointer' }}>
          <div style={{ fontFamily: cinzel, fontSize: 16, fontWeight: 600, color: '#e8d5a0', letterSpacing: '0.06em', textTransform: 'uppercase' }}>Grimoire des Héros</div>
        </div>

        {/* Nav droite */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 18 }}>
          <span
            onClick={() => onNavigate('fiches')}
            style={{ fontFamily: cinzel, fontSize: 13, color: page === 'fiches' ? '#e8d5a0' : '#a09070', borderBottom: page === 'fiches' ? '1px solid #8a6a3a' : '1px solid transparent', paddingBottom: 1, cursor: 'pointer', letterSpacing: '0.04em' }}
          >
            Mes Fiches
          </span>
          <div style={{ width: 1, height: 18, background: '#3a2c18' }} />
          <div style={{ width: 32, height: 32, borderRadius: '50%', background: '#5a3a8a', border: '1px solid #8a6ab0', display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: cinzel, fontSize: 13, fontWeight: 600, color: '#d4b8f0' }}>
            {utilisateur?.nom?.charAt(0).toUpperCase()}
          </div>
          <span style={{ fontFamily: cinzel, fontSize: 13, color: '#d4c4a0' }}>{utilisateur?.nom}</span>
          <div style={{ width: 1, height: 18, background: '#3a2c18' }} />
          <button onClick={handleLogout} style={{ background: 'transparent', border: 'none', color: '#c06050', fontFamily: cinzel, fontSize: 13, cursor: 'pointer' }}>Déconnexion</button>
        </div>
      </header>

      {/* ── Contenu ── */}
      <main style={{ flex: 1, padding: '28px 32px' }}>
        {children}
      </main>

      {/* ── Footer ── */}
      <footer style={{ background: '#110d05', borderTop: '1px solid #2e2410', padding: '10px 28px', textAlign: 'center', fontSize: 12, color: '#4a3a1a' }}>
        Grimoire des Héros
      </footer>
    </div>
  )
}