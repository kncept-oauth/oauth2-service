import { OidcProvider } from "@axa-fr/react-oidc"

const AxaFrOidcClient: React.FC = () => {
    const configuration = {
        client_id: "kncept-oidc-client",
        redirect_uri: window.location.origin + "/authentication/callback",
        silent_redirect_uri: window.location.origin + "/authentication/silent-callback",
        scope: "openid profile email api offline_access", // offline_access scope allow your client to retrieve the refresh_token
        authority: "http://localhost:8080",
        // service_worker_relative_url: "/OidcServiceWorker.js", // just comment that line to disable service worker mode
        service_worker_only: false,
        demonstrating_proof_of_possession: false, // demonstrating proof of possession will work only if access_token is accessible from the client (This is because WebCrypto API is not available inside a Service Worker)
      }

      console.log({
        redirect_uri1: `${window.location.protocol}//${window.location.host}/test-oidc/react-oidc/callback`,
        redirect_uri2: window.location.origin + "/authentication/callback",
      })
      

    

    return <div>
        AxaFrOidcClient impl<br/>
        <OidcProvider>
            Hello
    </OidcProvider>
    </div>
}

// const Body: React.FC = () => {
//     const { login, logout, renewTokens, isAuthenticated } = useOidc()
// }

export default AxaFrOidcClient