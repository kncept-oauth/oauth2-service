import { UserManager } from "oidc-client"
import React from "react"
import axios from 'axios'

const OidcClient: React.FC = (props) => {
    const [state, setState] = React.useState<any>({})
    console.log('state is', state)


    axios.get('http://localhost:8080/.well-known/openid-configuration')
            .then(response => console.log(response.data))

    React.useCallback(async () => {
        if (!state.initialized) {
            state.initialized = true
            setState(state)

//             axios.get('http://localhost:8080/.well-known/openid-configuration')
//             .then(response => setState((prev: any) => prev.wellKnown = response.data))
        }
    }, [state])

    // React.useEffect(() => {
    //     axios.get('http://localhost:8080/.well-known/openid-configuration')
    //       .then(response => {
    //         setState(prev => prev.wellKnown = response.data);
    //       })


    const userManager = new UserManager({
        authority: "http://localhost:8080/.well-known/openid-configuration",
        client_id: "kncept-oidc-client",
        client_secret: "kncept-oidc-client",
        redirect_uri: "http://localhost:3000/oidc-client-ts/redirect_uri"
    })
    // expects the following values:

    // issuer
    // authorization_endpoint
    // userinfo_endpoint
    // end_session_endpoint



    return <span>
        OIDC Client is a javascript (not typescript) library and is not yet implemented
    </span>
}

export default OidcClient