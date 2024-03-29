package com.wallet.crypto.trustapp.repository;

import android.text.TextUtils;

import com.wallet.crypto.trustapp.entity.NetworkInfo;
import com.wallet.crypto.trustapp.entity.Ticker;
import com.wallet.crypto.trustapp.service.TickerService;
import com.wallet.crypto.trustapp.util.WalletUtil;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.Single;

import static com.wallet.crypto.trustapp.C.ETHEREUM_NETWORK_NAME;
import static com.wallet.crypto.trustapp.C.ETH_SYMBOL;
import static com.wallet.crypto.trustapp.C.KOVAN_NETWORK_NAME;
import static com.wallet.crypto.trustapp.C.POA_NETWORK_NAME;
import static com.wallet.crypto.trustapp.C.POA_SYMBOL;
import static com.wallet.crypto.trustapp.C.ROPSTEN_NETWORK_NAME;
import static com.wallet.crypto.trustapp.C.RINKEBY_NETWORK_NAME;
import static com.wallet.crypto.trustapp.C.CLASSIC_NETWORK_NAME;
import static com.wallet.crypto.trustapp.C.ETC_SYMBOL;

public class EthereumNetworkRepository implements EthereumNetworkRepositoryType {

	private final NetworkInfo[] NETWORKS = new NetworkInfo[] {
			new NetworkInfo(ETHEREUM_NETWORK_NAME, ETH_SYMBOL,
                    "https://mainnet.infura.io/v3/f165b595cd184b2a848716830f9804b0",
                    "https://api.trustwalletapp.com/",
                    "https://etherscan.io/",1, true),

			new NetworkInfo(KOVAN_NETWORK_NAME, ETH_SYMBOL,
                    "https://kovan.infura.io/v3/f165b595cd184b2a848716830f9804b0",
                    "https://kovan.trustwalletapp.com/",
                    "https://kovan.etherscan.io", 42, false),
			new NetworkInfo(ROPSTEN_NETWORK_NAME, ETH_SYMBOL,
                    "https://ropsten.infura.io/v3/f165b595cd184b2a848716830f9804b0",
                    "https://ropsten.trustwalletapp.com/",
                    "https://ropsten.etherscan.io",3, false),
			new NetworkInfo(RINKEBY_NETWORK_NAME, ETH_SYMBOL,
					"https://rinkeby.infura.io/v3/f165b595cd184b2a848716830f9804b0",
					"https://rinkeby.trustwalletapp.com/",
					"https://rinkeby.etherscan.io",3, false),
	};

	private final PreferenceRepositoryType preferences;
    private final TickerService tickerService;
    private NetworkInfo defaultNetwork;
    private final Set<OnNetworkChangeListener> onNetworkChangedListeners = new HashSet<>();

    public EthereumNetworkRepository(PreferenceRepositoryType preferenceRepository, TickerService tickerService) {
		this.preferences = preferenceRepository;
		this.tickerService = tickerService;
		defaultNetwork = getByName(preferences.getDefaultNetwork());
		if (defaultNetwork == null) {
			defaultNetwork = NETWORKS[0]; // defaultNetwork = NETWORKS[0];
		}
	}

	private NetworkInfo getByName(String name) {
		if (!TextUtils.isEmpty(name)) {
			for (NetworkInfo NETWORK : NETWORKS) {
				if (name.equals(NETWORK.name)) {
					return NETWORK;
				}
			}
		}
		return null;
	}

	@Override
	public NetworkInfo getDefaultNetwork() {
		return defaultNetwork;
	}

	@Override
	public void setDefaultNetworkInfo(NetworkInfo networkInfo) {
		defaultNetwork = networkInfo;
		preferences.setDefaultNetwork(defaultNetwork.name);

		for (OnNetworkChangeListener listener : onNetworkChangedListeners) {
		    listener.onNetworkChanged(networkInfo);
        }
		WalletUtil.rpcServerUrl=defaultNetwork.rpcServerUrl;
	}

	@Override
	public NetworkInfo[] getAvailableNetworkList() {
		return NETWORKS;
	}

	@Override
	public void addOnChangeDefaultNetwork(OnNetworkChangeListener onNetworkChanged) {
        onNetworkChangedListeners.add(onNetworkChanged);
	}

    @Override
    public Single<Ticker> getTicker() {
        return Single.fromObservable(tickerService
                .fetchTickerPrice(getDefaultNetwork().symbol));
    }
}
