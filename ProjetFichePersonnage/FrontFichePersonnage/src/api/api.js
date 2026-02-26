const API_URL = 'http://localhost:8080/api'

async function requete(chemin, options = {}) {
  const res = await fetch(`${API_URL}${chemin}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options
  })
  const data = await res.json()
  if (!res.ok) throw new Error(data.erreur || 'Erreur serveur')
  return data
}

// ===== AUTH =====
export function login(nom, motdepasse) {
  return requete('/login', {
    method: 'POST',
    body: JSON.stringify({ nom, motdepasse })
  })
}

export function signup(nom, motdepasse) {
  return requete('/signup', {
    method: 'POST',
    body: JSON.stringify({ nom, motdepasse })
  })
}

export function logout() {
  return requete('/logout', { method: 'POST' })
}

export function getUtilisateur() {
  return requete('/utilisateur')
}

// ===== FICHES =====
export function listerFiches() {
  return requete('/fiches')
}

export function creerFiche(nom) {
  return requete('/fiches', {
    method: 'POST',
    body: JSON.stringify({ nom })
  })
}

export function getFiche(id) {
  return requete(`/fiches/${id}`)
}

export function supprimerFiche(id) {
  return requete(`/fiches/${id}`, { method: 'DELETE' })
}

// ===== MODULES =====
export function modifierPortrait(idFiche, image) {
  return requete(`/fiches/${idFiche}/portrait`, {
    method: 'PUT',
    body: JSON.stringify({ image })
  })
}

export function modifierBiographie(idFiche, texte) {
  return requete(`/fiches/${idFiche}/biographie`, {
    method: 'PUT',
    body: JSON.stringify({ texte })
  })
}

export function ajouterStatistique(idFiche, nom, valeur) {
  return requete(`/fiches/${idFiche}/statistiques`, {
    method: 'POST',
    body: JSON.stringify({ nom, valeur })
  })
}

export function ajouterCompetence(idFiche, nom) {
  return requete(`/fiches/${idFiche}/competences`, {
    method: 'POST',
    body: JSON.stringify({ nom })
  })
}

export function ajouterEquipement(idFiche, nom) {
  return requete(`/fiches/${idFiche}/equipements`, {
    method: 'POST',
    body: JSON.stringify({ nom })
  })
}

export function modifierPositionModule(idFiche, module, posX, posY) {
  return requete(`/fiches/${idFiche}/module/position`, {
    method: 'PUT',
    body: JSON.stringify({ module, posX, posY })
  })
}

export function modifierTailleModule(idFiche, module, largeur, hauteur) {
  return requete(`/fiches/${idFiche}/module/taille`, {
    method: 'PUT',
    body: JSON.stringify({ module, largeur, hauteur })
  })
}
