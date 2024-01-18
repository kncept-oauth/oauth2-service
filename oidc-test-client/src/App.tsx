import { FC } from 'react'
import './App.css'
import { BrowserRouter, Link, Navigate, Route, Routes, useParams } from 'react-router-dom';
import OidcClient from './OidcClient';
import OidcClientTs from './OidcClientTs';
import ReactOidcContext from './ReactOidcContext';
import AxaFrOidcClient from './AxaFrOidcClient';

interface OidcTestClientImpl {
  view: FC,
  enabled: boolean
}

const oidcTestClients: Record<string, OidcTestClientImpl> = {
  'oidc-client': {enabled: false, view: () => <OidcClient />},
  'oidc-client-ts': {enabled: false, view: OidcClientTs},
  'react-oidc': {enabled: true, view: ReactOidcContext},
  'axafr-oidc': {enabled: false, view: AxaFrOidcClient},
  
}

function App() {
  return (
    <div className="App">
      {/* <header className="App-header">
        Kncept OIDC Test Harness
      </header> */}
      <BrowserRouter>
      <Routes>
      <Route path="/" Component={ListTestClients} />

      <Route path="/test-oidc/:id/*" Component={OidcTestClientSelector} />
      {/* <Route path="/test-oidc/:id"> */}
        {/* <OidcTestClientSelector /> */}
      {/* </Route> */}
      <Route path="*" Component={ListTestClients} />
      </Routes>
      <br /><Link to="/"> home </Link>
      </BrowserRouter>
      
    </div>
  );
}


const OidcTestClientSelector: React.FC = () => {
  let { id } = useParams()

  const testClient = oidcTestClients[id || ""]
  if (!testClient) {
    return <span>No test client with id: {id}</span>
  }
  if (!testClient.enabled) {
    return <span>Test client currently disabled: {id}</span>
  }
  return testClient.view({})
}

const ListTestClients: React.FC = () => {
  
  const availableClientIds = Object.keys(oidcTestClients).filter(id => oidcTestClients[id].enabled)
  if (availableClientIds.length === 1) return <Navigate to={`/test-oidc/${availableClientIds[0]}`} />
  return (
    <span>
      Please choose a test implementation to drive:
      <ul>
      {availableClientIds.map(id => <li key={id}><Link to={`/test-oidc/${id}`}> {id} </Link></li>)}
      </ul>
    </span>
  )
}

export default App;
