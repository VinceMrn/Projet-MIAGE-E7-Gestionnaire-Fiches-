import { useState, useEffect } from 'react'
import * as api from '../../api/api'
import Portrait from '../modules/Portrait'
import Biographie from '../modules/Biographie'
import Statistiques from '../modules/Statistiques'
import Competences from '../modules/Competences'
import Equipements from '../modules/Equipements'

const MODULES_DEFAUT = [
  { id: 'portrait',     label: 'Portrait' },
  { id: 'biographie',   label: 'Biographie' },
  { id: 'statistiques', label: 'Statistiques' },
  { id: 'competences',  label: 'Compétences' },
  { id: 'equipements',  label: 'Équipements' },
]

const TYPES_CUSTOM = [
  { id: 'texte', label: 'Texte libre',   desc: 'Un champ texte comme Biographie' },
  { id: 'liste', label: 'Liste',          desc: "Une liste d'éléments comme Compétences" },
  { id: 'stats', label: 'Statistiques',  desc: 'Des paires nom + valeur' },
]

function ModuleCustom({ module, onSupprimer, idFiche, onSaved }) {
  const [collapsed, setCollapsed] = useState(false)
  const [contenu, setContenu] = useState(module.type === 'texte' ? (module.contenuTexte || '') : (module.contenuListe || module.contenuStats || []))
  const [nomItem, setNomItem] = useState('')
  const [valItem, setValItem] = useState('')
  const [edition, setEdition] = useState(false)
  const [erreur, setErreur] = useState('')

  const cinzel = "'Cinzel', serif"
  const crimson = "'Crimson Text', Georgia, serif"

  const handleAjouterItem = (e) => {
    e.preventDefault()
    if (!nomItem.trim()) return
    if (module.type === 'stats') {
      setContenu(prev => [...prev, { id: Date.now(), nom: nomItem, valeur: parseInt(valItem) || 0 }])
    } else {
      setContenu(prev => [...prev, { id: Date.now(), nom: nomItem }])
    }
    setNomItem('')
    setValItem('')
  }

  const handleSupprimerItem = (id) => setContenu(prev => prev.filter(i => i.id !== id))

  // Save to backend
  const handleSave = async () => {
    setErreur('')
    try {
      const payload = { nom: module.label, type: module.type }
      if (module.type === 'texte') payload.contenuTexte = contenu
      if (module.type === 'liste') payload.contenuListe = (contenu || []).map(i => i.nom || i)
      if (module.type === 'stats') payload.contenuStats = (contenu || []).map(i => ({ nom: i.nom, valeur: i.valeur }))
      await api.modifierModulePersonnalise(idFiche, module.id, payload)
      if (onSaved) await onSaved()
      setEdition(false)
    } catch (err) {
      setErreur(err.message)
    }
  }

  return (
    <div style={{ background: '#2a1f14', border: '1px solid #5c4a2a', borderRadius: 8, overflow: 'hidden', fontFamily: crimson }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '12px 16px 12px 36px', borderBottom: collapsed ? 'none' : '1px solid #3a2c18' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <span style={{ color: '#6a5a3a', fontSize: 14 }}>✧</span>
          <h3 style={{ fontFamily: cinzel, fontSize: 13, fontWeight: 600, color: '#e8d5a0', margin: 0, letterSpacing: '0.05em', textTransform: 'uppercase' }}>{module.label}</h3>
        </div>
        <div style={{ display: 'flex', gap: 10 }}>
          <button onClick={() => setCollapsed(!collapsed)} style={{ background: 'transparent', border: 'none', color: '#6a5a3a', cursor: 'pointer', fontSize: 14 }}>{collapsed ? '∨' : '∧'}</button>
          <button onClick={() => onSupprimer(module.id)} style={{ background: 'transparent', border: 'none', color: '#8a4030', cursor: 'pointer', fontSize: 13, fontFamily: crimson }}>✕</button>
        </div>
      </div>

      {!collapsed && (
        <div style={{ padding: 16 }}>

          {module.type === 'texte' && (
            edition ? (
              <div>
                <textarea
                  value={contenu}
                  onChange={e => setContenu(e.target.value)}
                  rows={5}
                  style={{ width: '100%', background: '#1e1509', border: '1px solid #4a3a1a', color: '#d4c4a0', padding: '8px 12px', borderRadius: 5, fontFamily: crimson, fontSize: 13, outline: 'none', resize: 'none', boxSizing: 'border-box', marginBottom: 8 }}
                  placeholder="Écrivez ici..."
                />
                <div style={{ display: 'flex', gap: 8 }}>
                  <button onClick={handleSave} style={{ background: '#4a7030', border: '1px solid #6a9040', color: '#c8e0a0', padding: '6px 14px', borderRadius: 5, fontFamily: cinzel, fontSize: 11, cursor: 'pointer' }}>Sauvegarder</button>
                  <button onClick={() => setEdition(false)} style={{ background: 'transparent', border: '1px solid #4a3a1a', color: '#8a7a5a', padding: '6px 14px', borderRadius: 5, fontFamily: cinzel, fontSize: 11, cursor: 'pointer' }}>Annuler</button>
                </div>
              </div>
            ) : (
              <div>
                <p style={{ fontSize: 14, color: '#c4b490', lineHeight: 1.7, margin: '0 0 12px', whiteSpace: 'pre-wrap' }}>
                  {contenu || <span style={{ color: '#5a4a2a', fontStyle: 'italic' }}>Aucun contenu</span>}
                </p>
                <button onClick={() => setEdition(true)} style={{ background: 'transparent', border: '1px solid #4a3a1a', color: '#8a7a5a', padding: '5px 14px', borderRadius: 4, fontFamily: cinzel, fontSize: 11, cursor: 'pointer' }}>✎ Modifier</button>
              </div>
            )
          )}

          {erreur && <div style={{ marginTop: 8, color: '#f0a0a0', fontSize: 12 }}>{erreur}</div>}

          {module.type === 'liste' && (
            <div>
              {contenu.length === 0
                ? <p style={{ color: '#5a4a2a', fontStyle: 'italic', fontSize: 13, marginBottom: 10 }}>Aucun élément</p>
                : <div style={{ display: 'flex', flexDirection: 'column', gap: 6, marginBottom: 10 }}>
                    {contenu.map(item => (
                      <div key={item.id} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', background: '#1e1509', border: '1px solid #3a2c18', borderRadius: 5, padding: '8px 12px' }}>
                        <span style={{ fontSize: 13, color: '#b0a080' }}>{item.nom}</span>
                        <button onClick={() => handleSupprimerItem(item.id)} style={{ background: 'transparent', border: 'none', color: '#8a4030', fontSize: 12, cursor: 'pointer' }}>✕</button>
                      </div>
                    ))}
                  </div>
              }
              <form onSubmit={handleAjouterItem} style={{ display: 'flex', gap: 8 }}>
                <input value={nomItem} onChange={e => setNomItem(e.target.value)} placeholder="Nouvel élément..." required
                  style={{ flex: 1, background: '#1e1509', border: '1px solid #4a3a1a', color: '#d4c4a0', padding: '7px 10px', borderRadius: 5, fontFamily: crimson, fontSize: 13, outline: 'none' }} />
                <button type="submit" style={{ background: '#4a7030', border: '1px solid #6a9040', color: '#c8e0a0', padding: '7px 12px', borderRadius: 5, fontFamily: cinzel, fontSize: 11, cursor: 'pointer' }}>+</button>
              </form>
            </div>
          )}

          {module.type === 'stats' && (
            <div>
              {contenu.length === 0
                ? <p style={{ color: '#5a4a2a', fontStyle: 'italic', fontSize: 13, marginBottom: 10 }}>Aucune statistique</p>
                : <div style={{ display: 'flex', flexDirection: 'column', gap: 6, marginBottom: 10 }}>
                    {contenu.map(item => (
                      <div key={item.id} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', background: '#1e1509', border: '1px solid #3a2c18', borderRadius: 5, padding: '8px 12px' }}>
                        <span style={{ fontSize: 13, color: '#b0a080' }}>{item.nom}</span>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                          <span style={{ fontSize: 13, color: '#c4a86a', fontWeight: 600 }}>{item.valeur}</span>
                          <button onClick={() => handleSupprimerItem(item.id)} style={{ background: 'transparent', border: 'none', color: '#8a4030', fontSize: 12, cursor: 'pointer' }}>✕</button>
                        </div>
                      </div>
                    ))}
                  </div>
              }
              <form onSubmit={handleAjouterItem} style={{ display: 'flex', gap: 8 }}>
                <input value={nomItem} onChange={e => setNomItem(e.target.value)} placeholder="Nom..." required
                  style={{ flex: 1, background: '#1e1509', border: '1px solid #4a3a1a', color: '#d4c4a0', padding: '7px 10px', borderRadius: 5, fontFamily: crimson, fontSize: 13, outline: 'none' }} />
                <input value={valItem} onChange={e => setValItem(e.target.value)} placeholder="Val" type="number"
                  style={{ width: 60, background: '#1e1509', border: '1px solid #4a3a1a', color: '#d4c4a0', padding: '7px 10px', borderRadius: 5, fontFamily: crimson, fontSize: 13, outline: 'none' }} />
                <button type="submit" style={{ background: '#4a7030', border: '1px solid #6a9040', color: '#c8e0a0', padding: '7px 12px', borderRadius: 5, fontFamily: cinzel, fontSize: 11, cursor: 'pointer' }}>+</button>
              </form>
            </div>
          )}
        </div>
      )}
    </div>
  )
}

export default function FicheDetail({ idFiche, onRetour }) {
  const [fiche, setFiche] = useState(null)
  const [chargement, setChargement] = useState(true)
  const [erreur, setErreur] = useState('')
  const [modules, setModules] = useState(MODULES_DEFAUT)
  const [modalVisible, setModalVisible] = useState(false)
  const [nomCustom, setNomCustom] = useState('')
  const [typeCustom, setTypeCustom] = useState('texte')

  const chargerFiche = async () => {
    setChargement(true)
    setErreur('')
    try {
      const data = await api.getFiche(idFiche)
      setFiche(data)
      // merge server custom modules into UI modules
      try {
        const server = data.modulesPersonnalises || []
        const customs = server.map(m => ({
          id: m.id,
          label: m.nom,
          type: m.type,
          custom: true,
          contenuTexte: m.contenuTexte,
          contenuListe: m.contenuListe,
          contenuStats: m.contenuStats,
        }))
        setModules(prev => {
          const ids = new Set(prev.map(x => x.id))
          const merged = prev.slice()
          customs.forEach(c => {
            if (!ids.has(c.id)) merged.push(c)
            else {
              // update existing entry with server data
              for (let i = 0; i < merged.length; i++) {
                if (merged[i].id === c.id) merged[i] = { ...merged[i], ...c }
              }
            }
          })
          return merged
        })
      } catch (e) {}
    } catch (err) {
      setErreur(err.message)
    } finally {
      setChargement(false)
    }
  }

  useEffect(() => { chargerFiche() }, [idFiche])

  const handleToggleModule = (mod) => {
    const existe = modules.find(m => m.id === mod.id)
    if (existe) {
      setModules(prev => prev.filter(m => m.id !== mod.id))
    } else {
      setModules(prev => [...prev, mod])
    }
  }

  const handleAjouterCustom = () => {
    if (!nomCustom.trim()) return
    (async () => {
      try {
        const payload = { nom: nomCustom.trim(), type: typeCustom }
        if (typeCustom === 'texte') payload.contenuTexte = ''
        if (typeCustom === 'liste') payload.contenuListe = []
        if (typeCustom === 'stats') payload.contenuStats = []
        await api.ajouterModulePersonnalise(idFiche, payload)
        await chargerFiche()
      } catch (err) {
        setErreur(err.message)
      } finally {
        setNomCustom('')
        setTypeCustom('texte')
        setModalVisible(false)
      }
    })()
  }

  const handleSupprimerCustom = async (id) => {
    try {
      // attempt server delete; fallback to local removal
      await api.supprimerModulePersonnalise(idFiche, id)
      await chargerFiche()
    } catch (err) {
      setModules(prev => prev.filter(m => m.id !== id))
      setErreur(err.message)
    }
  }

  const renderModule = (mod) => {
    if (mod.custom) return <ModuleCustom module={mod} onSupprimer={handleSupprimerCustom} idFiche={idFiche} onSaved={chargerFiche} />
    if (!fiche) return null
    switch (mod.id) {
      case 'portrait':     return <Portrait     portrait={fiche.portrait}         idFiche={idFiche} onUpdate={chargerFiche} />
      case 'biographie':   return <Biographie   biographie={fiche.biographie}     idFiche={idFiche} onUpdate={chargerFiche} />
      case 'statistiques': return <Statistiques statistiques={fiche.statistiques} idFiche={idFiche} onUpdate={chargerFiche} />
      case 'competences':  return <Competences  competences={fiche.competences}   idFiche={idFiche} onUpdate={chargerFiche} />
      case 'equipements':  return <Equipements  equipements={fiche.equipements}   idFiche={idFiche} onUpdate={chargerFiche} />
      default: return null
    }
  }

  const cinzel = "'Cinzel', serif"
  const crimson = "'Crimson Text', Georgia, serif"

  if (chargement) return <p style={{ color: '#6a5a3a', textAlign: 'center', fontStyle: 'italic', fontFamily: crimson, marginTop: 40 }}>Chargement du grimoire...</p>

  if (erreur) return (
    <div style={{ fontFamily: crimson }}>
      <button onClick={onRetour} style={{ color: '#c4a86a', background: 'transparent', border: 'none', cursor: 'pointer', fontFamily: cinzel, fontSize: 13, marginBottom: 16 }}>← Retour</button>
      <div style={{ background: '#4a1515', border: '1px solid #8a3030', color: '#f0a0a0', padding: '10px 14px', borderRadius: 6, fontSize: 13 }}>{erreur}</div>
    </div>
  )

  return (
    <div style={{ fontFamily: crimson, color: '#d4c4a0' }}>



      {/* Infos personnage */}
      <p style={{ fontSize: 13, color: '#8a7a5a', fontStyle: 'italic', marginBottom: 24 }}>
        {[fiche.classe, fiche.niveau && `Level ${fiche.niveau}`, fiche.alignement].filter(Boolean).join(' • ')}
      </p>

      {/* Modules */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
        {modules.map(m => (
          <div key={m.id}>{renderModule(m)}</div>
        ))}
      </div>

      {/* Modal */}
      {modalVisible && (
        <div onClick={() => setModalVisible(false)} style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.6)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 }}>
          <div
            onClick={e => e.stopPropagation()}
            style={{ background: '#2a1f14', border: '1px solid #5c4a2a', borderRadius: 10, padding: 24, width: '100%', maxWidth: 380, fontFamily: crimson, maxHeight: '85vh', overflowY: 'auto' }}
          >
            {/* Modal header */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
              <div style={{ fontFamily: cinzel, fontSize: 15, color: '#e8d5a0', fontWeight: 600 }}>Ajouter un Module</div>
              <button onClick={() => setModalVisible(false)} style={{ background: 'transparent', border: 'none', color: '#6a5a3a', fontSize: 18, cursor: 'pointer', padding: 0 }}>✕</button>
            </div>

            {/* Modules standard */}
            <div style={{ fontFamily: cinzel, fontSize: 11, color: '#6a5a3a', letterSpacing: '0.08em', textTransform: 'uppercase', marginBottom: 10 }}>Modules standard</div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 6, marginBottom: 16 }}>
              {MODULES_DEFAUT.map(mod => {
                const actif = !!modules.find(m => m.id === mod.id)
                return (
                  <div key={mod.id} onClick={() => handleToggleModule(mod)} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '9px 12px', background: '#1e1509', border: `1px solid ${actif ? '#4a7030' : '#3a2c18'}`, borderRadius: 5, cursor: 'pointer' }}>
                    <span style={{ fontFamily: cinzel, fontSize: 12, color: '#c4b080', letterSpacing: '0.04em' }}># {mod.label}</span>
                    <span style={{ fontFamily: cinzel, fontSize: 10, color: actif ? '#4a7030' : '#5c4a2a', border: `1px solid ${actif ? '#4a7030' : '#3a2c18'}`, borderRadius: 3, padding: '2px 7px' }}>
                      {actif ? 'actif' : '+ ajouter'}
                    </span>
                  </div>
                )
              })}
            </div>

            {/* Séparateur */}
            <div style={{ height: 1, background: '#3a2c18', margin: '16px 0' }} />

            {/* Module personnalisé */}
            <div style={{ fontFamily: cinzel, fontSize: 11, color: '#6a5a3a', letterSpacing: '0.08em', textTransform: 'uppercase', marginBottom: 10 }}>Module personnalisé</div>
            <input
              value={nomCustom}
              onChange={e => setNomCustom(e.target.value)}
              placeholder="Nom du module..."
              style={{ width: '100%', background: '#1e1509', border: '1px solid #4a3a1a', color: '#d4c4a0', padding: '8px 12px', borderRadius: 5, fontFamily: crimson, fontSize: 13, outline: 'none', boxSizing: 'border-box', marginBottom: 10 }}
            />

            <div style={{ fontFamily: cinzel, fontSize: 11, color: '#6a5a3a', letterSpacing: '0.06em', marginBottom: 8 }}>Type de contenu</div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 6, marginBottom: 14 }}>
              {TYPES_CUSTOM.map(t => (
                <div key={t.id} onClick={() => setTypeCustom(t.id)} style={{ display: 'flex', alignItems: 'center', gap: 10, padding: '8px 12px', background: '#1e1509', border: `1px solid ${typeCustom === t.id ? '#4a7030' : '#3a2c18'}`, borderRadius: 5, cursor: 'pointer' }}>
                  <div style={{ width: 14, height: 14, borderRadius: '50%', border: `2px solid ${typeCustom === t.id ? '#4a7030' : '#3a2c18'}`, background: typeCustom === t.id ? '#4a7030' : 'transparent', flexShrink: 0 }} />
                  <div>
                    <div style={{ fontFamily: cinzel, fontSize: 12, color: '#c4b080' }}>{t.label}</div>
                    <div style={{ fontSize: 11, color: '#6a5a3a', fontStyle: 'italic' }}>{t.desc}</div>
                  </div>
                </div>
              ))}
            </div>

            <button onClick={handleAjouterCustom} style={{ width: '100%', background: '#4a7030', border: '1px solid #6a9040', color: '#c8e0a0', padding: 9, borderRadius: 5, fontFamily: cinzel, fontSize: 12, cursor: 'pointer', fontWeight: 600 }}>
              + Créer ce module
            </button>
          </div>
        </div>
      )}
    </div>
  )
}