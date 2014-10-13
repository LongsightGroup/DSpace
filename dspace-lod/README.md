# Installation instructions #

## Maven Artifact Locations ##

For convenience sake, dspace-oceanlink maven artifacts reside in a git repository on github:
```
[GIT](https://github.com/dspace-oceanlink/mvn-repo.git)
 ```
## Build Configuration ##

Build configuration require that you supply dependencies and addon module pom.xml file within your build source.

Changes are neccessary to the following files

* [dspace-parent/pom.xml](../pom.xml)
* [dspace/modules/pom.xml](../dspace/modules/pom.xml)
* [dspace/modules/additions/pom.xml](../dspace/modules/additions/pom.xml)
* [dspace/modules/xmlui/pom.xml](../dspace/modules/xmlui/pom.xml)

Two additional new poms will be needed to complete  support for Sesame:

* [dspace/modules/sesame/pom.xml](../dspace/modules/sesame/pom.xml)
* [dspace/modules/workbench/pom.xml](../dspace/modules/workbench/pom.xml)

### dspace-parent/pom.xml Changes ###

Add the following dependency management versions to the [dspace-parent.pom](../pom.xml). These changes provide version numbers to the rest of the dependencies added below.

```
          <dependency>
             <groupId>org.dspace</groupId>
             <artifactId>dspace-lod-api</artifactId>
             <version>3.2</version>
             <type>jar</type>
          </dependency>
          <dependency>
             <groupId>org.dspace</groupId>
             <artifactId>dspace-lod-xmlui</artifactId>
             <version>3.2</version>
             <type>jar</type>
             <classifier>classes</classifier>
          </dependency>
          <dependency>
             <groupId>org.dspace</groupId>
             <artifactId>dspace-lod-xmlui</artifactId>
             <version>3.2</version>
             <type>war</type>
          </dependency>
```

### dspace/modules/pom.xml POM Changes ###

Additional build module sections should be added to support the new sesame and workbench webapplications.

```
        <profile>
            <id>dspace-sesame</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <modules>
                <module>sesame</module>
            </modules>
        </profile>
	        
	<profile>
            <id>dspace-sesame-workbench</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <modules>
                <module>workbench</module>
            </modules>
        </profile>
```
        
### dspace/modules/additions/pom.xml POM Changes ###

Add the following in [additions.pom](../dspace/modules/additions/pom.xml)

Under the repositories section 
```
   <repositories>
        <repository>
            <id>ocean-link-release-repository</id>
            <name>DSpace OceanLink Release Repository</name>
            <url>https://raw.githubusercontent.com/dspace-oceanlink/mvn-repo/master/releases</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
   </repositories>
```   
Add the following under the dependencies section

```
  <dependency>
     <groupId>org.dspace</groupId>
     <artifactId>dspace-lod-api</artifactId>
  </dependency>
```

### dspace/modules/xmlui/pom.xml POM Changes ###

Add the following under the dependency section in [xmlui.pom](../dspace/modules/xmlui/pom.xml)

Under the repositories section 
```
   <repositories>
        <repository>
            <id>ocean-link-release-repository</id>
            <name>DSpace OceanLink Release Repository</name>
            <url>https://raw.githubusercontent.com/dspace-oceanlink/mvn-repo/master/releases</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
   </repositories>
```

Add the following under the dependencies section

```
  <dependency>
     <groupId>org.dspace</groupId>
     <artifactId>dspace-lod-xmlui</artifactId>
     <type>war</type>
  </dependency>

  <dependency>
     <groupId>org.dspace</groupId>
     <artifactId>dspace-lod-xmlui</artifactId>
     <type>jar</type>
     <classifier>classes</classifier>
  </dependency>
```

### dspace/modules/sesame/pom.xml POM Changes ###

Create the sesame module directory inside dspace/modules

Add the following sesame [dspace/modules/sesame/pom.xml](../dspace/modules/sesame/pom.xml) to dspace/modules/sesame


### dspace/modules/workbench/pom.xml POM Changes ###

Create the workbench module directory inside dspace/modules

Optionally, add the workbench [dspace/modules/workbench/pom.xml](../dspace/modules/workbench/pom.xml) to dspace/modules/workbench

## Configuration in dspace/config/spring/api/lod.xml ##

To properly configure the Service Manager services needed to interact with the sesame triplestore, a configuration in spring is needed. The following example is provided [dspace/config/spring/api/lod.xml](../dspace/config/spring/api/lod.xml)

### SesameIndexingService ###

Similar to other one-off services such as DSpace Discovery and DSpace Statistics, an indexing service is provided for Sesame that supports communication between DSpace Consumers, UI and the triplestore.  A sample configuration preconfigured for OceanLink is provided in the source.

```
<bean id="org.dspace.app.sesame.SesameIndexingService" class="org.dspace.app.sesame.SesameIndexingServiceImpl"/>
```

The Service is configured via one or more DSpaceRepositoryProviders that can be tuned to meet specific requirements for instantiation and population of new RDF repositories within the Sesame triplestore service. The provided configuration supplies an Sesame "NativeStore" configuration that is sufficent for basic population and retrieval of RDF.

Placeholders within the configuration will be replaced with values from your maven build.properties and dspace.cfg during maven build and ant deployment.
```
    <bean id="oceanLinkSesameProvider"  class="org.dspace.adapters.DSpaceRepositoryProvider">
        <constructor-arg ref="org.dspace.services.ConfigurationService"/>
        <constructor-arg ref="org.dspace.services.RequestService"/>
        <property name="manager">
            <bean class="org.openrdf.repository.manager.RemoteRepositoryManager">
                <constructor-arg value="${dspace.baseUrl}/sesame"/>
            </bean>
        </property>
        <property name="config">
            <bean class="org.openrdf.repository.config.RepositoryConfig">
                <constructor-arg value="dspace"/>
                <constructor-arg>
                    <!--
                    Alternative Sail Configurations May be Created and Configured at this point
                    Examples would include, Memory Sail, Native Sail, RDBMS Sail, Virtuoso Sail
                    -->
                    <bean class="org.openrdf.repository.sail.config.SailRepositoryConfig">
                        <constructor-arg>
                            <bean class="org.openrdf.sail.nativerdf.config.NativeStoreConfig"/>
                        </constructor-arg>
                    </bean>
                </constructor-arg>
            </bean>
        </property>
        <property name="adapters">
            <list>
                <bean class="org.dspace.adapters.oceanlink.OLBitstreamAdapter"/>
                <bean class="org.dspace.adapters.oceanlink.OLBundleAdapter"/>
                <bean class="org.dspace.adapters.oceanlink.OLItemAdapter"/>
                <bean class="org.dspace.adapters.oceanlink.OLCollectionAdapter"/>
                <bean class="org.dspace.adapters.oceanlink.OLCommunityAdapter"/>
                <bean class="org.dspace.adapters.oceanlink.OLSiteAdapter">
                    <property name="title" value="${dspace.name} Ocean Link Dataset"/>
                    <property name="description" value="RDF data extracted from ${dspace.name} for OceanLink"/>
                    <property name="creator" value="${dspace.name}"/>
                    <property name="homepage" value="${dspace.url}"/>
                    <property name="sparqlEndPoint" value="${dspace.baseUrl}/sesame/repositories/dspace"/>
                </bean>
            </list>
        </property>
    </bean>
```

## Configuration in dspace.cfg ##

Enabling sesame consumer in dspace.cfg:

The consumer should be enabled by adding sesame to the list of consumers in dspace.cfg
```
eg: event.dispatcher.default.consumers = versioning, search, browse, eperson, harvester, sesame
 ```
Add sesame class and filters as below:
```
event.consumer.sesame.class = org.dspace.app.sesame.SesameEventConsumer
event.consumer.sesame.filters = All+All
```

## Commandline Support for reindexing Sesame ##

Commandline routine to generate rdf statements in the triplestore

From the bin directory of deployment folder, run the following
```
./dspace dsrun org.dspace.app.sesame.SesameIndexClient -b

```