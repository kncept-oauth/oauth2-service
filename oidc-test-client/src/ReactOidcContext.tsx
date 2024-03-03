// https://www.npmjs.com/package/react-oidc-context
import { UserManager, WebStorageStateStore } from 'oidc-client-ts';
import React from 'react'
import { AuthProvider, AuthProviderProps, useAuth, } from "react-oidc-context"
import { Route, Routes, useNavigate } from 'react-router-dom'
import {Log} from "oidc-client-ts"

class WrappedStorage implements Storage {
    prefix: string
    storage: Storage
    length: number = 0
    constructor(prefix: string, storage: Storage) {
        this.prefix = prefix
        this.storage = storage
        this.length = storage.length
        console.log('init ' + prefix + ' ' + storage)
    }
    clear(): void {
        this.storage.clear()
        this.length = this.storage.length
    }
    getItem(key: string): string | null {
        console.log(this.prefix + ' getItem ' + key)
        return this.storage.getItem(key)
    }
    key(index: number): string | null {
        console.log(this.prefix + ' key ' + index)
        return this.storage.key(index)
    }
    removeItem(key: string): void {
        console.log(this.prefix + ' removeItem ' + key)
        this.storage.removeItem(key)
        this.length = this.storage.length
    }
    setItem(key: string, value: string): void {
        console.log(this.prefix + ' setItem ' + key + ' ' + value)
        this.storage.setItem(key, value)
        this.length = this.storage.length
    }
}

export default function ReactOidcContext() {
//     Log.setLogger(console)
//     Log.setLevel(Log.DEBUG)
    const oidcConfig : AuthProviderProps = {
        authority: "http://localhost:8080", // --> http://localhost:8080/.well-known/openid-configuration
        client_id: "kncept-oidc-client",
        redirect_uri: `${window.location.protocol}//${window.location.host}/test-oidc/react-oidc-context/callback`,
        loadUserInfo: true,
//         userStore: new WebStorageStateStore({store: new WrappedStorage('userStore', window.localStorage) }),
//         stateStore: new WebStorageStateStore({store: new WrappedStorage('stateStore', window.localStorage) }),
        userStore: new WebStorageStateStore({store: window.localStorage }),
        stateStore: new WebStorageStateStore({store: window.localStorage }),
      }

    //   console.log('oidcConfig', oidcConfig)

    //   const userManager = undefined
    const userManager = React.useMemo(() => new UserManager(oidcConfig), [oidcConfig])

    return <AuthProvider
        userManager={userManager}
    >
         <div>r oidc</div>
         <Routes>
            <Route path="/" Component={() => <OidcNestedApp userManager={userManager} />}/>
            {/* <Route path="/callback" Component={OidcCallback} /> */}
            <Route path="/callback" Component={() => <OidcCallback userManager={userManager} />} />
            <Route path="*" Component={() => <OidcNestedApp userManager={userManager} />}/>
         </Routes>
         {/* <OidcNestedApp />  */}

        </AuthProvider>
}

const OidcCallback: React.FC<{userManager: UserManager}> = ({userManager}) => {
    const navigate = useNavigate()
    userManager.signinCallback()
    .then(user => {
        window.history.replaceState(
            {},
            document.title,
            window.location.pathname,
          )
//           React.useEffect() ??
        navigate('/test-oidc/react-oidc-context')
    })
    .catch(reason => {
        console.log('user manager catch')
        console.log(reason)
    })

    // const auth = useAuth()
    // auth.signincallback
    return <div>Processing callback, please wait</div>
}

const OidcNestedApp: React.FC<{userManager: UserManager}> = ({userManager}) => {
    const auth = useAuth()

    switch (auth.activeNavigator) {
        case "signinSilent":
            return <div>Signing you in...</div>
        case "signoutRedirect":
            return <div>Signing you out...</div>
    }

    if (auth.isLoading) {
        return <div>Loading...</div>
    }

    if (auth.error) {
        return <div>Oops... {auth.error.message}</div>
    }

    if (auth.isAuthenticated) {
        return (
        <div>
            Hello {auth.user?.profile.sub}{" "}
            <button onClick={() => void auth.removeUser()}>Log out</button>
        </div>
        )
    }

    return <button onClick={() => {
        // userManager.signinRedirect()
        auth.signinRedirect()
    }}>Log in</button>
}