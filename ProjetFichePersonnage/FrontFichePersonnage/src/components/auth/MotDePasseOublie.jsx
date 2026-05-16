import { useState } from "react";
import { useAuth } from "../../context/AuthContext";
import bgImage from '../../assets/Gemini_Generated_Image_kl696hkl696hkl69.png'

export default function MotDePasseOublie({ onRetour }) {
  const { getQuestionSecrete, reinitialiserMotDePasse } = useAuth();

  // 1 = saisie identifiant, 2 = saisie reponse + nouveau mdp
  const [etape, setEtape] = useState(1);
  const [identifiant, setIdentifiant] = useState("");
  const [question, setQuestion] = useState("");
  const [reponse, setReponse] = useState("");
  const [nouveauMdp, setNouveauMdp] = useState("");
  const [confirmMdp, setConfirmMdp] = useState("");
  const [msg, setMsg] = useState(null); // { type: 'erreur'|'succes', texte }

  // Etape 1 : recuperer la question secrete a partir de l'identifiant
  const handleEtape1 = async (e) => {
    e.preventDefault();
    setMsg(null);
    if (!identifiant.trim()) {
      setMsg({ type: "erreur", texte: "Identifiant requis." });
      return;
    }
    try {
      const data = await getQuestionSecrete(identifiant.trim());
      setQuestion(data.question);
      setEtape(2);
    } catch (err) {
      // Message generique pour ne pas leak l'existence d'un compte
      setMsg({ type: "erreur", texte: "Recuperation indisponible pour cet utilisateur." });
    }
  };

  // Etape 2 : verifier la reponse et appliquer le nouveau mdp
  const handleEtape2 = async (e) => {
    e.preventDefault();
    setMsg(null);
    if (!reponse.trim() || !nouveauMdp || !confirmMdp) {
      setMsg({ type: "erreur", texte: "Tous les champs sont requis." });
      return;
    }
    if (nouveauMdp !== confirmMdp) {
      setMsg({ type: "erreur", texte: "La confirmation ne correspond pas." });
      return;
    }
    try {
      await reinitialiserMotDePasse(identifiant.trim(), reponse.trim(), nouveauMdp);
      setMsg({ type: "succes", texte: "Mot de passe reinitialise. Tu peux te connecter avec ton nouveau mot de passe." });
    } catch (err) {
      // Generique : on ne dit pas si c'est l'identifiant ou la reponse qui est faux
      setMsg({ type: "erreur", texte: "Identifiant ou reponse incorrect(e)." });
    }
  };

  // Retour a l'etape 1 : on clear les saisies de l'etape 2
  const retourEtape1 = () => {
    setEtape(1);
    setQuestion("");
    setReponse("");
    setNouveauMdp("");
    setConfirmMdp("");
    setMsg(null);
  };

  return (
    <div
      className="min-h-screen w-full flex items-center justify-center bg-cover bg-center bg-no-repeat"
      style={{ backgroundImage: `url(${bgImage})` }}
    >
      <div className="card-medieval rounded-xl p-10 w-full max-w-md animate-fade-in-up">

        <div className="flex items-center justify-center mb-2">
          <span className="text-gold opacity-40 text-2xl tracking-[0.5em] font-medieval">🔑</span>
        </div>

        <h1 className="font-medieval text-3xl font-bold text-center mb-1 gold-shimmer">
          Mot de passe oublie
        </h1>
        <p className="font-body text-parchment-dark text-center text-sm italic mb-2">
          Retrouve l'acces a ton compte grace a ta question secrete
        </p>

        <div className="divider-medieval my-6" />

        {msg && (
          <div className={
            (msg.type === "erreur"
              ? "bg-blood-dark/40 border-blood/50 text-red-300"
              : "bg-moss-dark/40 border-moss/50 text-green-300") +
            " border text-sm p-3 rounded-lg mb-5 font-body text-center"
          }>
            {msg.type === "erreur" ? "⚠ " : "✦ "}{msg.texte}
          </div>
        )}

        {/* Etape 1 : saisie de l'identifiant */}
        {etape === 1 && msg?.type !== "succes" && (
          <form onSubmit={handleEtape1}>
            <div className="mb-7">
              <label className="block text-gold/70 text-xs font-medieval uppercase tracking-widest mb-2">
                Nom d'aventurier
              </label>
              <input
                type="text"
                value={identifiant}
                onChange={(e) => setIdentifiant(e.target.value)}
                className="w-full input-medieval rounded-lg py-3 px-4"
                placeholder="Votre nom..."
                required
              />
            </div>
            <button type="submit" className="w-full btn-medieval py-3 px-4 rounded-lg">
              Continuer
            </button>
          </form>
        )}

        {/* Etape 2 : reponse + nouveau mdp */}
        {etape === 2 && msg?.type !== "succes" && (
          <form onSubmit={handleEtape2}>
            <div className="mb-5">
              <label className="block text-gold/70 text-xs font-medieval uppercase tracking-widest mb-2">
                Question secrete
              </label>
              <div className="font-body text-parchment-dark italic p-3 bg-black/20 rounded-lg">
                {question}
              </div>
            </div>

            <div className="mb-5">
              <label className="block text-gold/70 text-xs font-medieval uppercase tracking-widest mb-2">
                Reponse
              </label>
              <input
                type="text"
                value={reponse}
                onChange={(e) => setReponse(e.target.value)}
                className="w-full input-medieval rounded-lg py-3 px-4"
                placeholder="Votre reponse..."
                required
              />
            </div>

            <div className="mb-5">
              <label className="block text-gold/70 text-xs font-medieval uppercase tracking-widest mb-2">
                Nouveau mot de passe
              </label>
              <input
                type="password"
                value={nouveauMdp}
                onChange={(e) => setNouveauMdp(e.target.value)}
                className="w-full input-medieval rounded-lg py-3 px-4"
                placeholder="Votre nouveau secret..."
                required
              />
            </div>

            <div className="mb-7">
              <label className="block text-gold/70 text-xs font-medieval uppercase tracking-widest mb-2">
                Confirmation
              </label>
              <input
                type="password"
                value={confirmMdp}
                onChange={(e) => setConfirmMdp(e.target.value)}
                className="w-full input-medieval rounded-lg py-3 px-4"
                placeholder="Confirmez..."
                required
              />
            </div>

            <button type="submit" className="w-full btn-medieval py-3 px-4 rounded-lg mb-3">
              Reinitialiser le mot de passe
            </button>
            <button
              type="button"
              onClick={retourEtape1}
              className="w-full text-parchment-dark hover:text-gold transition text-sm font-body underline"
            >
              ← Changer d'identifiant
            </button>
          </form>
        )}

        <div className="divider-medieval my-6" />

        <p className="font-body text-parchment-dark text-sm text-center">
          <button
            onClick={onRetour}
            className="text-gold hover:text-gold-light transition font-semibold"
          >
            Retour a la connexion
          </button>
        </p>

      </div>
    </div>
  );
}
