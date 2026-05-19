import { useEffect } from 'react'
import FicheDetail from './FicheDetail'

export default function ApercuFiche({ idFiche, onFermer }) {
  const cinzel = "'Cinzel', serif"

  // Ferme l'apercu avec la touche Escape
  useEffect(() => {
    const onKey = (e) => { if (e.key === 'Escape') onFermer() }
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [onFermer])

  return (
    <div
      onClick={onFermer}
      style={{
        position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
        background: 'rgba(0, 0, 0, 0.78)',
        backdropFilter: 'blur(3px)',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        zIndex: 100,
        animation: 'apercuFadeIn 0.2s ease-out',
      }}
    >
      <style>{`
        @keyframes apercuFadeIn { from { opacity: 0; } to { opacity: 1; } }
        @keyframes apercuSlideUp { from { transform: translateY(16px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }
      `}</style>

      <div
        onClick={e => e.stopPropagation()}
        style={{
          background: '#1a1208',
          border: '1px solid #5c4a2a',
          borderRadius: 12,
          width: '95vw',
          maxWidth: 1400,
          height: '92vh',
          padding: '20px 24px',
          position: 'relative',
          boxShadow: '0 24px 80px rgba(0, 0, 0, 0.6)',
          animation: 'apercuSlideUp 0.25s ease-out',
        }}
      >
        <button
          onClick={onFermer}
          title="Fermer l'aperçu (Echap)"
          style={{
            position: 'absolute', top: 14, right: 14, zIndex: 10,
            background: '#2a1f14', border: '1px solid #5c4a2a',
            color: '#c4a86a', fontFamily: cinzel, fontSize: 14, fontWeight: 600,
            width: 34, height: 34, borderRadius: '50%', cursor: 'pointer',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
          }}
        >
          ✕
        </button>

        <FicheDetail
          idFiche={idFiche}
          modeInitial="affiche"
          onRetour={onFermer}
          embarquee
        />
      </div>
    </div>
  )
}
