import React from 'react';
import './App.css';
import { BrowserRouter, Link, Route, Routes } from 'react-router-dom';
import OidcClient from './oidc-client/OidcClient';
import OidcClientTs from './oidc-client-ts/OidcClientTs';

function App() {
  return (
    <div className="App">
      {/* <header className="App-header">
        Kncept OIDC Test Harness
      </header> */}
      <BrowserRouter>
      <Routes>
      <Route path="/" Component={Home} />
      <Route path="/oidc-client/" Component={OidcClient} />
      <Route path="/oidc-client-ts/" Component={OidcClientTs} />
      </Routes>
      <br /><Link to="/"> home </Link>
      </BrowserRouter>
      
    </div>
  );
}

function Home() {
  return (
    <span>
      Please choose a test implementation to drive:
      <ul>
      <li><Link to="/oidc-client/"> oidc-client </Link></li>
      <li><Link to="/oidc-client-ts/"> oidc-client-ts </Link></li>
      </ul>
    </span>
  )
}

export default App;
