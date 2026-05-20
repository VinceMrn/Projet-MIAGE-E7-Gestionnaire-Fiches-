const API_URL = 'http://localhost:8080/api'

async function requete(chemin, options = {}) {
  const sessionId = localStorage.getItem('sessionId')
  const res = await fetch(`${API_URL}${chemin}`, {
    headers: { 'Content-Type': 'application/json', 'X-Session-Id': sessionId },
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

export function modifierIdentifiant(nom) {
  return requete('/utilisateur/identifiant', {
    method: 'PUT',
    body: JSON.stringify({ nom })
  })
}

export function modifierMotDePasse(ancien, nouveau) {
  return requete('/utilisateur/motdepasse', {
    method: 'PUT',
    body: JSON.stringify({ ancien, nouveau })
  })
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

// Export : retourne { nom, data (base64) }
export function exporterFiche(id) {
  return requete(`/fiches/${id}/export`)
}

// Import : envoie le base64 du fichier .fiche
export function importerFiche(base64) {
  return requete('/fiches/import', {
    method: 'POST',
    body: JSON.stringify({ data: base64 })
  })
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

// Elements existants chez l'utilisateur (toutes fiches confondues, sans doublons)
export function listerCompetencesExistantes() {
  return requete('/fiches/elements/competences')
}

export function listerEquipementsExistants() {
  return requete('/fiches/elements/equipements')
}

export function listerStatistiquesExistantes() {
  return requete('/fiches/elements/statistiques')
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

// Question secrete pour recuperation mot de passe

export function definirQuestionSecrete(question, reponse) {
  return requete('/utilisateur/questionsecrete', {
    method: 'PUT',
    body: JSON.stringify({ question, reponse })
  })
}

export function getQuestionSecrete(nom) {
  return requete('/utilisateur/getquestionsecrete', {
    method: 'POST',
    body: JSON.stringify({ nom })
  })
}

export function reinitialiserMotDePasse(nom, reponse, nouveau) {
  return requete('/utilisateur/reinitialisermotdepasse', {
    method: 'POST',
    body: JSON.stringify({ nom, reponse, nouveau })
  })
}
