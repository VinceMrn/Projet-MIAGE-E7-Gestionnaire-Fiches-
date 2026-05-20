export default function BarreVie({ label, hp, hpMax, couleur }) {
  // Pourcentage de vie restante, bloqué entre 0% et 100%
  const pct = Math.max(0, Math.min(100, (hp / hpMax) * 100))

  const cinzel = "'Cinzel', serif"
  const crimson = "'Crimson Text', Georgia, serif"

  return (
    <div style={{ marginBottom: 14 }}>
      {/* Ligne label + HP en chiffres */}
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 5, fontFamily: cinzel, fontSize: 13, color: '#d4c4a0' }}>
        <span>{label}</span>
        <span style={{ color: pct < 25 ? '#f08060' : '#a09070' }}>{hp} / {hpMax} PV</span>
      </div>

      {/* Fond de la barre */}
      <div style={{ background: '#1e1608', border: '1px solid #3a2c18', borderRadius: 4, height: 18, overflow: 'hidden' }}>
        {/* Barre de vie animée */}
        <div style={{
          width: `${pct}%`,
          height: '100%',
          background: couleur || '#4a7030',
          transition: 'width 0.5s ease',
          borderRadius: 3
        }} />
      </div>
    </div>
  )
}
