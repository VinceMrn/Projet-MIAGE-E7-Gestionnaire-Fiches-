import { useState } from 'react'
import { useAuth } from '../../context/AuthContext'

export default function ParametresCompte({ onRetour }) {
  const { utilisateur, modifierIdentifiant, modifierMotDePasse } = useAuth()

  const [nouveauNom, setNouveauNom] = useState(utilisateur?.nom || '')
  const [ancienMdp, setAncienMdp] = useState('')
  const [nouveauMdp, setNouveauMdp] = useState('')
  const [confirmMdp, setConfirmMdp] = useState('')

  const [msgIdent, setMsgIdent] = useState(null) // { type, texte }
  const [msgMdp, setMsgMdp] = useState(null)

  const cinzel = "'Cinzel', serif"
  const crimson = "'Crimson Text', Georgia, serif"

  const handleIdentifiant = async (e) => {
    e.preventDefault()
    setMsgIdent(null)
    if (!nouveauNom.trim() || nouveauNom === utilisateur?.nom) {
      setMsgIdent({ type: 'erreur', texte: 'Saisis un identifiant différent.' })
      return
    }
    try {
      await modifierIdentifiant(nouveauNom.trim())
      setMsgIdent({ type: 'succes', texte: 'Identifiant mis à jour.' })
    } catch (err) {
      setMsgIdent({ type: 'erreur', texte: err.message })
    }
  }

  const handleMotDePasse = async (e) => {
    e.preventDefault()
    setMsgMdp(null)
    if (nouveauMdp !== confirmMdp) {
      setMsgMdp({ type: 'erreur', texte: 'La confirmation ne correspond pas.' })
      return
    }
    if (nouveauMdp.length < 1) {
      setMsgMdp({ type: 'erreur', texte: 'Le nouveau mot de passe est vide.' })
      return
    }
    try {
      await modifierMotDePasse(ancienMdp, nouveauMdp)
      setMsgMdp({ type: 'succes', texte: 'Mot de passe mis à jour.' })
      setAncienMdp(''); setNouveauMdp(''); setConfirmMdp('')
    } catch (err) {
      setMsgMdp({ type: 'erreur', texte: err.message })
    }
  }

  const styleInput = { width: '100%', background: '#1e1509', border: '1px solid #4a3a1a', color: '#d4c4a0', padding: '9px 12px', borderRadius: 5, fontFamily: crimson, fontSize: 14, outline: 'none', boxSizing: 'border-box', marginBottom: 12 }
  const styleLabel = { display: 'block', fontFamily: cinzel, fontSize: 11, color: '#6a5a3a', letterSpacing: '0.08em', textTransform: 'uppercase', marginBottom: 6 }
  const styleBtnPrimary = { background: '#4a7030', border: '1px solid #6a9040', color: '#c8e0a0', padding: '10px 18px', borderRadius: 5, fontFamily: cinzel, fontSize: 12, cursor: 'pointer', fontWeight: 600, letterSpacing: '0.04em' }
  const styleSection = { background: '#2a1f14', border: '1px solid #5c4a2a', borderRadius: 8, padding: 24, marginBottom: 18 }
  const styleH = { fontFamily: cinzel, fontSize: 14, fontWeight: 600, color: '#e8d5a0', margin: '0 0 4px', letterSpacing: '0.06em', textTransform: 'uppercase' }
  const styleSub = { fontSize: 13, color: '#8a7a5a', fontStyle: 'italic', margin: '0 0 18px' }

  const renderMsg = (msg) => {
    if (!msg) return null
    const isErr = msg.type === 'erreur'
    return (
      <div style={{
        background: isErr ? '#4a1515' : '#1a3a18',
        border: `1px solid ${isErr ? '#8a3030' : '#3a7a30'}`,
        color: isErr ? '#f0a0a0' : '#a0e0a0',
        padding: '8px 12px', borderRadius: 5, fontSize: 13, marginBottom: 12
      }}>{msg.texte}</div>
    )
  }

  return (
    <div style={{ fontFamily: crimson, color: '#d4c4a0', maxWidth: 520, margin: '0 auto' }}>

      <button onClick={onRetour} style={{ background: 'transparent', border: 'none', color: '#a09070', fontFamily: cinzel, fontSize: 12, cursor: 'pointer', letterSpacing: '0.04em', marginBottom: 24 }}>
        ← Retour
      </button>

      <h2 style={{ fontFamily: cinzel, fontSize: 22, fontWeight: 600, color: '#e8d5a0', margin: '0 0 6px', letterSpacing: '0.08em', textTransform: 'uppercase' }}>
        Paramètres du compte
      </h2>
      <p style={{ fontSize: 13, color: '#8a7a5a', fontStyle: 'italic', margin: '0 0 28px' }}>
        Modifie ton identifiant ou ton mot de passe.
      </p>

      {/* Identifiant */}
      <form onSubmit={handleIdentifiant} style={styleSection}>
        <h3 style={styleH}>Identifiant</h3>
        <p style={styleSub}>Connecté en tant que <strong style={{ color: '#c4a86a' }}>{utilisateur?.nom}</strong></p>
        {renderMsg(msgIdent)}
        <label style={styleLabel}>Nouvel identifiant</label>
        <input type="text" value={nouveauNom} onChange={e => setNouveauNom(e.target.value)} style={styleInput} required />
        <button type="submit" style={styleBtnPrimary}>Mettre à jour l'identifiant</button>
      </form>

      {/* Mot de passe */}
      <form onSubmit={handleMotDePasse} style={styleSection}>
        <h3 style={styleH}>Mot de passe</h3>
        <p style={styleSub}>Choisis un nouveau mot de passe.</p>
        {renderMsg(msgMdp)}
        <label style={styleLabel}>Mot de passe actuel</label>
        <input type="password" value={ancienMdp} onChange={e => setAncienMdp(e.target.value)} style={styleInput} required />
        <label style={styleLabel}>Nouveau mot de passe</label>
        <input type="password" value={nouveauMdp} onChange={e => setNouveauMdp(e.target.value)} style={styleInput} required />
        <label style={styleLabel}>Confirmation</label>
        <input type="password" value={confirmMdp} onChange={e => setConfirmMdp(e.target.value)} style={styleInput} required />
        <button type="submit" style={styleBtnPrimary}>Mettre à jour le mot de passe</button>
      </form>
    </div>
  )
}
