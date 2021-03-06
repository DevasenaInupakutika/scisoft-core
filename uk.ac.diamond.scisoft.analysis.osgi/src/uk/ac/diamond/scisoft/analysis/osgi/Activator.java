package uk.ac.diamond.scisoft.analysis.osgi;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import uk.ac.diamond.scisoft.analysis.IAnalysisService;
import uk.ac.diamond.scisoft.analysis.io.ILoaderFactoryExtensionService;

public class Activator implements BundleActivator {

	private BundleContext context=null;
	@Override
	public void start(BundleContext c) throws Exception {
		context = c;
		Hashtable<String, String> props = new Hashtable<String, String>(1);
		props.put("description", "A service used by the LoaderFactory to read extension points.");
		context.registerService(ILoaderFactoryExtensionService.class, new LoaderFactoryExtensionService(), props);
		
		props = new Hashtable<String, String>(1);
		props.put("description", "A service which replaces concrete classes in the scisoft.analysis plugin.");
		context.registerService(IAnalysisService.class, new AnalysisServiceImpl(), props);
	}

	@Override
	public void stop(BundleContext c) throws Exception {
		context = null;
	}

}
