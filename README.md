# oidc-auth-test
Demo for authentication against OIDC provider

# Requirements

Running this demo requires:

- nodejs & npm
- Java
- Clojure CLI

Follow these instructions to [install the Clojure CLI](https://clojure.org/guides/install_clojure) if you haven't already:

...or if you'd prefer to install into your home directory:

```
curl -O https://download.clojure.org/install/linux-install-1.11.1.1208.sh
chmod +x linux-install-1.11.1.1208.sh
./linux-install-1.11.1.1208.sh --prefix $HOME
```

# Setup

Use `npm` to install the JavaScript dependencies to the `node_modules` directory:
```
git clone https://github.com/dfornika/oidc-auth-test.git
cd oidc-auth-test
npm install
```

# Start

```
npx shadow-cljs watch app -A:dev
```

The demo will be served at http://localhost:8080