const API_URL = 'http://localhost:8080/api'

async function requete(chemin, body) {
  const sessionId = localStorage.getItem('sessionId')
  const res = await fetch(`${API_URL}${chemin}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'X-Session-Id': sessionId || '' },
    body: JSON.stringify(body)
  })
  const data = await res.json()
  if (!res.ok) throw new Error(data.erreur || 'Erreur serveur')
  return data
}

// Génère un ennemi selon le niveau (1 à 5)
// Retourne { nom, hp, attaque, defense }
export function genererEnnemi(niveau) {
  return requete('/combat/ennemi', { niveau })
}

// Calcule un tour d'attaque complet.
// valeurStatAttaque : valeur de la stat choisie pour attaquer
// valeurStatDefense : valeur de la stat choisie pour défendre
// bonusDefense      : bonus de défense temporaire (ex: +5 si Bouclier actif)
// defenseEnnemi / attaqueEnnemi : stats de l'ennemi actuel
// Retourne { degatsJoueur, degatsEnnemi, deJoueur, deEnnemi,
//            totalJoueur, totalEnnemi, typeJoueur, typeEnnemi, description }
export function attaquer(valeurStatAttaque, valeurStatDefense, bonusDefense, defenseEnnemi, attaqueEnnemi) {
  return requete('/combat/attaquer', {
    valeurStatAttaque, valeurStatDefense, bonusDefense, defenseEnnemi, attaqueEnnemi
  })
}

// Lance un dé à N faces avec un modificateur
// Retourne { de, modificateur, resultat, description }
export function lancerDe(faces, modificateur) {
  return requete('/combat/de', { faces, modificateur })
}
