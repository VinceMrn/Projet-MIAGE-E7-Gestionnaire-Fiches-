import { useState, useEffect, useRef } from 'react'
import * as api from '../../api/api'
import ApercuFiche from './ApercuFiche'

export default function ListeFiches({ onSelectFiche, onCreerFiche }) {
  const [fiches, setFiches] = useState([])
  const [chargement, setChargement] = useState(true)
  const [erreur, setErreur] = useState('')
  const [recherche, setRecherche] = useState('')
  const [tri, setTri] = useState('modifie')
  const [idApercu, setIdApercu] = useState(null)
  const inputImportRef = useRef(null)

  const chargerFiches = async () => {
    setChargement(true)
    setErreur('')
    try {
      const data = await api.listerFiches()
      setFiches(Array.isArray(data) ? data : [])
    } catch (err) {
      setErreur(err.message)
    } finally {
      setChargement(false)
    }
  }

  useEffect(() => { chargerFiches() }, [])

  const handleModeAffiche = (e, id) => {
    e.stopPropagation()
    setIdApercu(id)
  }

  const handleSupprimer = async (e, id) => {
    e.stopPropagation()
    if (!confirm('Supprimer cette fiche ?')) return
    try {
      await api.supprimerFiche(id)
      chargerFiches()
    } catch (err) {
      setErreur(err.message)
    }
  }

  // Export : recupere la fiche en base64 depuis l'API et declenche un telechargement .fiche
  const handleExporter = async (e, id, nom) => {
    e.stopPropagation()
    try {
      const { nom: nomServeur, data } = await api.exporterFiche(id)
      // Decode base64 -> bytes -> Blob
      const binaire = atob(data)
      const bytes = new Uint8Array(binaire.length)
      for (let i = 0; i < binaire.length; i++) bytes[i] = binaire.charCodeAt(i)
      const blob = new Blob([bytes], { type: 'application/octet-stream' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `${(nomServeur || nom || 'fiche').replace(/[^a-z0-9_-]+/gi, '_')}.fiche`
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      URL.revokeObjectURL(url)
    } catch (err) {
      setErreur(err.message)
    }
  }

  // Import : declenche le selecteur de fichier
  const handleClickImport = () => inputImportRef.current?.click()

  // Lit le fichier choisi en base64 puis appelle l'API d'import
  const handleFichierImport = async (e) => {
    const fichier = e.target.files?.[0]
    e.target.value = '' // reset pour permettre re-selection du meme fichier
    if (!fichier) return
    try {
      const base64 = await new Promise((resolve, reject) => {
        const reader = new FileReader()
        reader.onload = () => {
          const res = reader.result // "data:...;base64,XXXX"
          const virgule = res.indexOf(',')
          resolve(virgule >= 0 ? res.slice(virgule + 1) : res)
        }
        reader.onerror = () => reject(new Error('Lecture du fichier impossible'))
        reader.readAsDataURL(fichier)
      })
      await api.importerFiche(base64)
      chargerFiches()
    } catch (err) {
      setErreur(err.message)
    }
  }

  const fichesFiltrees = fiches
    .filter(f => f.nom?.toLowerCase().includes(recherche.toLowerCase()))
    .sort((a, b) => {
      if (tri === 'az') return a.nom.localeCompare(b.nom)
      if (tri === 'za') return b.nom.localeCompare(a.nom)
      return 0
    })

  const cinzel = "'Cinzel', serif"
  const crimson = "'Crimson Text', Georgia, serif"

  const s = {
    sectionRow: { display: 'flex', alignItems: 'flex-end', justifyContent: 'space-between', marginBottom: 4 },
    title: { fontFamily: cinzel, fontSize: 24, color: '#e8d5a0', fontWeight: 600 },
    count: { fontSize: 13, color: '#6a5a3a', fontStyle: 'italic' },
    btnPrimary: { background: '#4a7030', border: '1px solid #6a9040', color: '#c8e0a0', padding: '9px 20px', borderRadius: 6, fontFamily: cinzel, fontSize: 13, cursor: 'pointer', fontWeight: 600 },
    btnSecondary: { background: '#2e2410', border: '1px solid #4a3a1a', color: '#a09070', padding: '9px 16px', borderRadius: 6, fontFamily: cinzel, fontSize: 13, cursor: 'pointer', fontWeight: 600 },
    actionsTop: { display: 'flex', gap: 10 },
    expBtn: { background: 'transparent', border: 'none', color: '#6a8a40', fontSize: 12, cursor: 'pointer', fontFamily: crimson, marginRight: 8 },
    afficheBtn: { background: 'transparent', border: 'none', color: '#c4a86a', fontSize: 12, cursor: 'pointer', fontFamily: crimson, marginRight: 8 },
    controls: { display: 'flex', gap: 10, margin: '16px 0 20px' },
    search: { flex: 1, background: '#110d05', border: '1px solid #4a3a1a', color: '#d4c4a0', padding: '9px 14px', borderRadius: 6, fontFamily: crimson, fontSize: 14, outline: 'none' },
    select: { background: '#110d05', border: '1px solid #4a3a1a', color: '#a09070', padding: '9px 12px', borderRadius: 6, fontFamily: crimson, fontSize: 13, outline: 'none', cursor: 'pointer' },
    erreur: { background: '#4a1515', border: '1px solid #8a3030', color: '#f0a0a0', padding: '10px 14px', borderRadius: 6, marginBottom: 14, fontSize: 13 },
    grid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: 14 },
    card: { background: '#110d05', border: '1px solid #4a3a1a', borderRadius: 8, overflow: 'hidden', cursor: 'pointer' },
    portrait: { height: 155, background: '#1e1608', display: 'flex', alignItems: 'center', justifyContent: 'center', borderBottom: '1px solid #2e2410' },
    cardBody: { padding: '12px 14px' },
    cardName: { fontFamily: cinzel, fontSize: 13, color: '#e8d5a0', fontWeight: 600, marginBottom: 4 },
    cardInfo: { fontSize: 12, color: '#7a6a4a', fontStyle: 'italic', marginBottom: 8 },
    cardMeta: { fontSize: 11, color: '#4a3a1a', display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
    delBtn: { background: 'transparent', border: 'none', color: '#8a4030', fontSize: 12, cursor: 'pointer', fontFamily: crimson },
  }

  return (
    <div style={{ fontFamily: crimson, color: '#d4c4a0' }}>

      {/* Titre + bouton */}
      <div style={s.sectionRow}>
        <div>
          <div style={s.title}>Mes Personnages</div>
          <div style={s.count}>{fichesFiltrees.length} personnage{fichesFiltrees.length !== 1 ? 's' : ''}</div>
        </div>
        <div style={s.actionsTop}>
          <button style={s.btnSecondary} onClick={handleClickImport}>Importer</button>
          <button style={s.btnPrimary} onClick={onCreerFiche}>+ Nouveau Personnage</button>
        </div>
      </div>

      {/* Input fichier cache pour l'import */}
      <input
        ref={inputImportRef}
        type="file"
        accept=".fiche"
        style={{ display: 'none' }}
        onChange={handleFichierImport}
      />

      {/* Erreur */}
      {erreur && <div style={s.erreur}>{erreur}</div>}

      {/* Recherche + tri */}
      <div style={s.controls}>
        <input style={s.search} placeholder="Rechercher des personnages..." value={recherche} onChange={e => setRecherche(e.target.value)} />
        <select style={s.select} value={tri} onChange={e => setTri(e.target.value)}>
          <option value="modifie">Récemment Modifié</option>
          <option value="az">Nom A–Z</option>
          <option value="za">Nom Z–A</option>
        </select>
      </div>

      {/* Chargement */}
      {chargement && <p style={{ color: '#6a5a3a', textAlign: 'center', fontStyle: 'italic' }}>Chargement du grimoire...</p>}

      {/* Vide */}
      {!chargement && fichesFiltrees.length === 0 && (
        <div style={{ textAlign: 'center', padding: '60px 0' }}>
          <p style={{ color: '#6a5a3a', fontSize: 16, fontStyle: 'italic', marginBottom: 16 }}>Aucun personnage dans ce grimoire</p>
          <button style={s.btnPrimary} onClick={onCreerFiche}>Créer mon premier personnage</button>
        </div>
      )}

      {/* Modal apercu */}
      {idApercu && <ApercuFiche idFiche={idApercu} onFermer={() => setIdApercu(null)} />}

      {/* Grille */}
      {!chargement && fichesFiltrees.length > 0 && (
        <div style={s.grid}>
          {fichesFiltrees.map(fiche => (
            <div key={fiche.id} style={s.card} onClick={() => onSelectFiche(fiche.id)}>
              <div style={s.portrait}>
                {fiche.portrait
                  ? <img src={fiche.portrait} alt="" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                  : <svg width="42" height="42" viewBox="0 0 24 24" fill="none" stroke="#4a3a1a" strokeWidth="1.2"><circle cx="12" cy="8" r="4" /><path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" /></svg>
                }
              </div>
              <div style={s.cardBody}>
                <div style={s.cardName}>{fiche.nom}</div>
                <div style={s.cardInfo}>{[fiche.classe, fiche.niveau && `Niveau ${fiche.niveau}`, fiche.alignement].filter(Boolean).join(' • ')}</div>
                <div style={s.cardMeta}>
                  <span>ID: {fiche.id}</span>
                  <span>
                    <button style={s.afficheBtn} onClick={e => handleModeAffiche(e, fiche.id)} title="Ouvrir en lecture seule">👁 Affiche</button>
                    <button style={s.expBtn} onClick={e => handleExporter(e, fiche.id, fiche.nom)}>Exporter</button>
                    <button style={s.delBtn} onClick={e => handleSupprimer(e, fiche.id)}>Supprimer</button>
                  </span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}