import { Component } from '@angular/core';

@Component({
  selector: 'app-faq',
  templateUrl: './faq.component.html',
  styleUrls: ['./faq.component.css'],
})
export class FaqComponent {
  selectedCategory: any = null;

  faqItems = [
    {
      title: 'Ma commande',
      icon: 'fas fa-shopping-cart',
      questions: [
        {
          question: 'Comment suivre ma commande ?',
          answer: 'Vous pouvez suivre votre commande dans votre espace client.',
        },
        {
          question: 'Puis-je modifier ma commande après paiement ?',
          answer:
            'Non, une fois payée, la commande ne peut plus être modifiée.',
        },
        {
          question: 'Dans combien de temps vais-je recevoir ma commande ?',
          answer:
            'Tout dépend du mode de livraison pour lequel vous avez opté, ainsi que du pays où vous souhaitez être livré(e). Sachez que notre entrepôt est ouvert du lundi au vendredi. Toute commande passée un samedi ou un dimanche est donc traitée le lundi. En cas de jour férié, la commande est traitée le jour suivant également.',
        },
        {
          question: 'Je souhaite annuler ma commande, est-ce possible ?',
          answer:
            "Si votre commande n’a pas encore été traitée par notre entrepôt, il nous est possible d'annuler et de procéder au remboursement de celle-ci.\nDans ce cas, contactez-nous au plus vite par mail via l'adresse: ethrea@gmail.com.\nSi la commande a été envoyée à l'entrepôt, il ne nous sera malheureusement plus possible de l'annuler.\nDans ce cas, si vous ne souhaitez pas conserver vos articles, il vous est possible de nous retourner vos produits.",
        },
      ],
    },
    {
      title: 'Ma livraison',
      icon: 'fas fa-truck',
      questions: [
        {
          question: 'Quels sont les délais de livraison ?',
          answer:
            'Les délais de livraison varient en fonction du mode de livraison choisi et de votre adresse. Vous pouvez consulter les délais estimés lors du passage de votre commande.',
        },
        {
          question: 'Quels sont les modes de livraison disponibles ?',
          answer:
            'Nous proposons plusieurs modes de livraison : livraison standard à domicile, livraison express à domicile et livraison en point relais.',
        },
        {
          question: 'Comment suivre ma livraison ?',
          answer:
            "Une fois votre commande expédiée, vous recevrez un email avec un lien de suivi pour suivre l'acheminement de votre colis en temps réel.",
        },
        {
          question: 'Que faire si ma commande est en retard ?',
          answer:
            "Si votre commande dépasse le délai de livraison estimé, veuillez vérifier le suivi de votre colis. Si le problème persiste, contactez notre service client à l'adresse : support@etherea.com.",
        },
        {
          question:
            'Que faire si je ne suis pas chez moi lors de la livraison ?',
          answer:
            'Si vous êtes absent lors de la livraison, le transporteur laissera un avis de passage avec des instructions pour récupérer votre colis ou programmer une nouvelle livraison.',
        },
        {
          question:
            'Puis-je modifier mon adresse de livraison après avoir passé commande ?',
          answer:
            "Si votre commande n'a pas encore été expédiée, vous pouvez modifier votre adresse de livraison dans votre compte ou en contactant notre service client.",
        },
        {
          question: 'Que faire si mon colis est perdu ou endommagé ?',
          answer:
            'Si votre colis est perdu ou endommagé, contactez notre service client dès que possible afin que nous puissions trouver une solution adaptée.',
        },
      ],
    },

    {
      title: 'Mon compte',
      icon: 'fas fa-user',
      questions: [
        {
          question: 'Comment créer un compte ?',
          answer:
            "Pour créer un compte, cliquez sur 'Inscription' en haut à droite du site, puis remplissez le formulaire avec vos informations personnelles.",
        },
        {
          question: "J'ai oublié mon mot de passe, comment le récupérer ?",
          answer:
            "Si vous avez oublié votre mot de passe, cliquez sur 'Mot de passe oublié ?' sur la page de connexion et suivez les instructions pour le réinitialiser.",
        },
        {
          question: 'Comment modifier mes informations personnelles ?',
          answer:
            "Vous pouvez modifier vos informations personnelles en vous connectant à votre compte, puis en accédant à la section 'Mes informations'.",
        },
        {
          question: 'Comment supprimer mon compte ?',
          answer:
            "Pour supprimer définitivement votre compte, veuillez contacter notre service client à l'adresse : support@etherea.com.",
        },
        {
          question: 'Mes informations personnelles sont-elles sécurisées ?',
          answer:
            'Oui, nous prenons la protection de vos données très au sérieux. Toutes vos informations sont stockées de manière sécurisée et ne sont pas partagées avec des tiers sans votre consentement.',
        },
      ],
    },

    {
      title: 'Retours & Remboursements',
      icon: 'fas fa-undo',
      questions: [
        {
          question: 'Comment retourner un article ?',
          answer:
            'Vous pouvez retourner un article dans un délai de 14 jours après réception. Rendez-vous dans votre espace client, sélectionnez la commande concernée et suivez les instructions de retour.',
        },
        {
          question: 'Quels sont les frais de retour ?',
          answer:
            "Les frais de retour sont à votre charge, sauf en cas d'erreur de notre part ou d'un article défectueux.",
        },
        {
          question: 'Sous quel délai vais-je être remboursé ?',
          answer:
            'Une fois votre retour reçu et validé, nous procédons au remboursement sous 7 à 10 jours ouvrés via le mode de paiement utilisé lors de votre achat.',
        },
        {
          question: 'Puis-je échanger un article ?',
          answer:
            "Nous ne proposons pas d'échange direct. Si vous souhaitez une autre taille ou un autre modèle, il faudra retourner l'article et passer une nouvelle commande.",
        },
        {
          question: "Que faire si l'article reçu est défectueux ou incorrect ?",
          answer:
            "Si l'article reçu est défectueux ou ne correspond pas à votre commande, contactez notre service client avec une photo du produit et votre numéro de commande pour obtenir un retour gratuit.",
        },
        {
          question: 'Comment suivre mon retour ?',
          answer:
            'Une fois votre colis retourné, vous recevrez un email de confirmation à chaque étape du processus de retour et de remboursement.',
        },
      ],
    },
    {
      title: 'Marque & Produits',
      icon: 'fas fa-leaf',
      questions: [
        {
          question: 'Quels types de produits proposez-vous ?',
          answer:
            "Nous proposons des crèmes hydratantes, des huiles naturelles et d'autres soins pour la peau et les cheveux, fabriqués avec des ingrédients de haute qualité.",
        },
        {
          question: 'Vos produits sont-ils naturels et bio ?',
          answer:
            "Oui, nos produits sont formulés avec des ingrédients naturels et issus de l'agriculture biologique, sans produits chimiques agressifs.",
        },
        {
          question: 'Vos produits sont-ils testés sur les animaux ?',
          answer:
            'Non, nous sommes une marque engagée et nous ne testons aucun de nos produits sur les animaux.',
        },
        {
          question: 'Conviennent-ils aux peaux sensibles ?',
          answer:
            'Oui, nos produits sont conçus pour être doux et adaptés aux peaux sensibles. Nous recommandons néanmoins de faire un test sur une petite zone avant utilisation.',
        },
        {
          question: 'Comment choisir le bon produit pour mon type de peau ?',
          answer:
            'Consultez la description de nos produits et notre guide sur le site pour savoir quel soin est adapté à votre type de peau (sèche, grasse, mixte, sensible).',
        },
        {
          question: 'Quelle est la durée de conservation de vos produits ?',
          answer:
            "Nos produits ont une durée de conservation moyenne de 12 à 24 mois après ouverture. Vérifiez l'étiquette pour plus de détails.",
        },
        {
          question: 'Vos huiles sont-elles adaptées aux cheveux ?',
          answer:
            "Oui, nos huiles sont idéales pour nourrir et hydrater les cheveux. Certaines conviennent mieux aux cheveux secs, d'autres aux cheveux fins ou bouclés.",
        },
        {
          question: 'Vos crèmes sont-elles adaptées aux femmes enceintes ?',
          answer:
            "La plupart de nos crèmes sont sûres pour les femmes enceintes, mais nous recommandons de vérifier la composition et de demander l'avis d'un professionnel de santé si besoin.",
        },
        {
          question:
            'Utilisez-vous des parfums ou des conservateurs dans vos produits ?',
          answer:
            'Nous utilisons uniquement des parfums naturels et des conservateurs doux pour garantir la fraîcheur et la sécurité de nos produits.',
        },
      ],
    },
    {
      title: 'Demandes Professionnelles',
      icon: 'fas fa-briefcase',
      questions: [
        {
          question: 'Proposez-vous des tarifs pour les professionnels ?',
          answer:
            "Oui, nous offrons des tarifs préférentiels pour les professionnels et les revendeurs. Contactez-nous à l'adresse pro@etherea.com pour plus d'informations.",
        },
        {
          question: 'Puis-je revendre vos produits dans ma boutique ?',
          answer:
            "Oui, nous collaborons avec des boutiques et des instituts. Envoyez-nous votre demande avec les détails de votre entreprise pour discuter d'un partenariat.",
        },
        {
          question: 'Proposez-vous des produits en gros ?',
          answer:
            'Oui, nous proposons la vente en gros pour les professionnels. Contactez notre service commercial pour obtenir un devis personnalisé.',
        },
        {
          question: 'Puis-je personnaliser des produits pour mon entreprise ?',
          answer:
            'Nous pouvons étudier la possibilité de personnalisation selon votre besoin (étiquetage, packaging, formulation). Contactez-nous pour en discuter.',
        },
        {
          question:
            'Avez-vous un programme d’affiliation ou de collaboration ?',
          answer:
            'Oui, nous proposons des collaborations avec des influenceurs, salons et professionnels du bien-être. Envoyez-nous votre demande à partenariats@etherea.com.',
        },
        {
          question: 'Comment devenir distributeur de votre marque ?',
          answer:
            'Si vous souhaitez distribuer nos produits, contactez-nous avec une présentation de votre activité. Nous vous répondrons dans les plus brefs délais.',
        },
      ],
    },
    {
      title: 'Carrières',
      icon: 'fas fa-users',
      questions: [
        {
          question: 'Comment postuler à un emploi chez vous ?',
          answer:
            "Vous pouvez consulter nos offres d’emploi sur notre site dans la section 'Carrière' et postuler en envoyant votre CV et lettre de motivation à recrutement@etherea.com.",
        },
        {
          question: 'Acceptez-vous les candidatures spontanées ?',
          answer:
            'Oui, nous étudions toutes les candidatures. Envoyez-nous votre CV et une lettre de motivation expliquant pourquoi vous souhaitez rejoindre notre équipe.',
        },
        {
          question: 'Quels types de postes proposez-vous ?',
          answer:
            'Nous recrutons dans divers domaines comme la production, le marketing, la logistique et la relation client. Consultez nos offres pour plus de détails.',
        },
        {
          question: 'Proposez-vous des stages ou des alternances ?',
          answer:
            'Oui, nous accueillons régulièrement des stagiaires et des alternants. Vous pouvez postuler en envoyant votre candidature à stage@etherea.com.',
        },
        {
          question: 'Comment se déroule le processus de recrutement ?',
          answer:
            'Après réception de votre candidature, notre équipe la examine. Si votre profil correspond, nous vous contacterons pour un entretien.',
        },
        {
          question: 'Puis-je travailler à distance ?',
          answer:
            "Certains postes permettent le télétravail partiel ou total. Les conditions sont précisées dans nos offres d'emploi.",
        },
      ],
    },

    {
      title: 'Programme de fidélité',
      icon: 'fas fa-gift',
      questions: [
        {
          question: 'Comment fonctionne votre programme de fidélité ?',
          answer:
            "Notre programme de fidélité vous permet d'accumuler des points à chaque achat. Ces points peuvent être échangés contre des réductions ou des cadeaux exclusifs.",
        },
        {
          question: "Comment puis-je m'inscrire au programme de fidélité ?",
          answer:
            "L'inscription est automatique dès votre première commande. Vous pouvez consulter votre solde de points dans votre espace client.",
        },
        {
          question: 'Comment gagner des points de fidélité ?',
          answer:
            'Vous gagnez des points à chaque achat, mais aussi en parrainant un ami ou en laissant un avis sur nos produits.',
        },
        {
          question: 'Comment utiliser mes points de fidélité ?',
          answer:
            'Vos points peuvent être convertis en bons de réduction ou en produits gratuits directement depuis votre espace client.',
        },
        {
          question: 'Les points de fidélité ont-ils une date d’expiration ?',
          answer:
            'Oui, les points sont valables pendant 12 mois après leur obtention. Assurez-vous de les utiliser avant leur expiration.',
        },
        {
          question:
            'Puis-je transférer mes points de fidélité à quelqu’un d’autre ?',
          answer:
            'Non, les points sont personnels et ne peuvent pas être transférés à un autre compte.',
        },
        {
          question:
            'Y a-t-il des niveaux de fidélité avec des avantages exclusifs ?',
          answer:
            'Oui, notre programme comprend plusieurs niveaux. Plus vous achetez, plus vous accédez à des avantages exclusifs (réductions, accès anticipé aux nouvelles collections, cadeaux).',
        },
      ],
    },
  ];
}
