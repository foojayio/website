---
title: "Write Your Own Service Discovery Client for Apache APISIX"
slug: "service-discovery-client-apache-apisix"
date: "2022-08-01T10:27:42+00:00"
lastmod: "2022-08-01T15:15:25+00:00"
description: "Most modern infrastructures are dynamic. Servers are cattle, not pets. Learn how to implement a dynamic node registry based on a YAML file."
canonical: "https://blog.frankel.ch/own-service-discovery-client-apisix/"
authors:
  - "nicolas-frankel"
image: "pexels-3996179.jpg"
categories:
  - "DevOps"
  - "Microservices"
  - "Tools"
tags:
related_posts:
  - "apisix-api-gateway"
  - "how-to-secure-your-web-apps-with-an-api-gateway"
  - "backend-for-front-end"
  - "backend-for-frontend-the-demo"
enlighterjs: true
frozen: false
---

API Gateways in general, and [Apache APISIX](https://apisix.apache.org/) in particular, provide a single entry point into one's information system.

This architecture allows for managing load balancing and failover over similar nodes.

For example, here's how you can create a route balanced over two nodes in Apache APISIX:

```bash
curl http://localhost:9080/apisix/admin/routes/1 -H 'X-API-KEY: edd1c9f034335f136f87ad84b625c8f1' -X PUT -i -d '{
  "uri": "/*",
  "upstream": {
    "type": "roundrobin",
    "nodes": {
      "192.168.0.1:80": 1,           # 1
      "192.168.0.2:80": 1            # 1
    }
  }
}'
```


1. Every request has a 50/50 chance of being sent to either node

It worked for a long time, but in this day and age, nodes are probably not pets but cattle: they come, and they go. Hence, it's essential to dynamically update the nodes list when it happens.

In this article, I'd like to explain how to do it.

Existing service discovery registries
-------------------------------------

Please don't reinvent the wheel! Apache APISIX comes with a bunch of existing service discovery registries out-of-the-box.

|                     Registry                     | Provider  |                                                                                                                                                                                                                                                                                                                                                             Description                                                                                                                                                                                                                                                                                                                                                             |                             Integration                             |
|--------------------------------------------------|-----------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------|
|                                                  |           |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | [Link](https://apisix.apache.org/docs/apisix/discovery/dns/)        |
| [Consul](https://www.consul.io/)                 | HashiCorp | > Consul uses service identities and traditional networking practices to help organizations securely connect applications running in any environment.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               | [Link](https://apisix.apache.org/docs/apisix/discovery/consul_kv/)  |
| [nacos](https://nacos.io/en-us/)                 | Alibaba   | > An easy-to-use dynamic service discovery, configuration and service management platform for building cloud native applications.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   | [Link](https://apisix.apache.org/docs/apisix/discovery/nacos/)      |
| [Eureka](https://github.com/Netflix/eureka/wiki) | Netflix   | > Eureka is a RESTful (Representational State Transfer) service that is primarily used in the AWS cloud for the purpose of discovery, load balancing and failover of middle-tier servers. It plays a critical role in Netflix mid-tier infra.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       | [Link](https://apisix.apache.org/docs/apisix/discovery/eureka/)     |
| [Zookeeper](https://zookeeper.apache.org/)       | Apache    | > ZooKeeper is a centralized service for maintaining configuration information, naming, providing distributed synchronization, and providing group services. > All of these kinds of services are used in some form or another by distributed applications. > Each time they are implemented there is a lot of work that goes into fixing the bugs and race conditions that are inevitable. > Because of the difficulty of implementing these kinds of services, applications initially usually skimp on them, which make them brittle in the presence of change and difficult to manage. > Even when done correctly, different implementations of these services lead to management complexity when the applications are deployed. | [Link](https://apisix.apache.org/docs/apisix/discovery/zookeeper/)  |
| [Kubernetes](https://kubernetes.io/)             |           | > Kubernetes, also known as K8s, is an open-source system for automating deployment, scaling, and management of containerized applications.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | [Link](https://apisix.apache.org/docs/apisix/discovery/kubernetes/) |

Before you write your own, make sure your platform is not listed above.

Setting up the environment
--------------------------

To ease my life and make it easier to reproduce the steps, I chose to use Docker and Docker Compose. Here's the sample that you're welcome to reuse **for development purposes**:

```yaml
version: "3"

services:
  apisix:
    image: apache/apisix:2.14.1-alpine                                  # 1
    volumes:
      - ./config/config.yaml:/usr/local/apisix/conf/config.yaml:ro      # 2
      - ./yaml:/usr/local/apisix/apisix/discovery/yaml:ro               # 3
      - ./sample:/var/apisix:ro                                         # 3
    ports:
      - "9080:9080"
      - "9090:9090"
    restart: always                                                     # 4
    depends_on:
      - etcd
  etcd:
    image: bitnami/etcd:3.5.2
    environment:
      ETCD_ENABLE_V2: "true"
      ALLOW_NONE_AUTHENTICATION: "yes"                                  # 5
      ETCD_ADVERTISE_CLIENT_URLS: "http://0.0.0.0:2397"
      ETCD_LISTEN_CLIENT_URLS: "http://0.0.0.0:2397"
    ports:
      - "2397:2397"                                                     # 6
```


1. Use the latest image at the time of this writing  

2. Minimal configuration, see the [complete file](https://github.com/ajavageek/apisix-yaml-service-discovery/blob/master/config/config.yaml) for more details  

3. Bear with me; I'll explain later  

4. Apache APISIX starts faster than etcd. It will look for etcd, concludes it's not available, and stop. We want to start it again afterward.  

5. Don't do this in production!  

6. If running on Docker Desktop with Kubernetes enabled, a port conflict occurs with the default port. We need to change it.

The use-case
------------

Let's imagine a YAML file that references the available nodes. An *ad hoc* process listens to changes to the topology: it re-generates the file with the new nodes. Our client reads the file regularly and updates its internal nodes list.

Here's the proposed structure:

```yaml
nodes:
  "192.168.1.62:81": 1
#END
```


Developing the discovery service client
---------------------------------------

To create a discovery client, the following structure is required:

```
yaml                             # 1
  |_ schema.lua                  # 2
  |_ init.lua                    # 3
```


1. Give it a name; `yaml` is as good as any other  

2. List configuration parameters - name, type, whether it's required, etc.  

3. For the code itself

For Apache APISIX to use the client, you need to set the `yaml` folder as a child folder of `/usr/local/apisix/apisix/discovery`; hence the mount in the Docker Compose file above.

The client needs to follow a specific structure.

```lua
local _M = {}

-- Initialize the client
function _M.init_worker()
end

-- Get available nodes.
--
-- @param service_name Not used
-- @treturn table
-- @return Available nodes, e.g., { [1] = { ["port"] = 81, ["host"] = 127.0.0.1, ["weight"] = 1 }}
function _M.nodes(service_name)
end

-- Dump existing nodes.
--
-- @return Debugging information
function dump_data()
end
```


Let's start with the easy part:

```lua
local nodes

function _M.nodes(service_name)
  return nodes                         -- 1
end
```


1. Return the `nodes` table. We fill the nodes in the `_M.init()` function

We want the client to read the YAML file regularly. For this, we can leverage the power of the [Lua Nginx module](https://github.com/openresty/lua-nginx-module). It's part of [OpenResty](https://openresty.org/en/), which Apache APISIX is built upon. The module offers additional APIs, and two of them are particularly useful:

* [`ngx.timer.at`](https://github.com/openresty/lua-nginx-module#ngxtimerat) to call a function after an initial delay
* [`ngx.timer.every`](https://github.com/openresty/lua-nginx-module#ngxtimerevery) to call a function repeatedly at regular intervals

```lua
local ngx_timer_at    = ngx.timer.at
local ngx_timer_every = ngx.timer.every

function _M.init_worker()
    ngx_timer_at(0, read_file)                 -- 1
    ngx_timer_every(20, read_file)             -- 2
end
```


1. Call the `read_file` function immediately  

2. Call the `read_file` function every 20 seconds

Now is time to write the `read_file` function.

```lua
local util = require("apisix.cli.util")                                   -- 1
local yaml = require("tinyyaml")                                          -- 2

local function read_file()
    local content, err = util.read_file("/var/apisix/nodes.yaml")         -- 3
    if not content then
        return
    end
    local nodes_conf, err = yaml.parse(content)                           -- 4
    if not nodes_conf then
        return
    end
    if not nodes then
        nodes = {}
    end
    for uri, weight in pairs(nodes_conf.nodes) do                         -- 5
        local host_port = {}
        for str in string.gmatch(uri, "[^:]+") do
            table.insert(host_port, str)                                  -- 6
        end
        local node = {
          host = host_port[1],
          port = tonumber(host_port[2]),
          weight = weight,
        }                                                                 -- 7
        table.insert(nodes, node)                                         -- 8
    end
end
```


1. Import the library to read file  

2. Import the library to convert YAML content to Lua tables  

3. Read the file  

4. Parse its content  

5. Iterate over the lines, which should be formatted as `":":`. I'm too lazy to handle all corner cases, be my guest.  

6. Parse each key - the `":"` string  

7. Create a Lua table for each node  

8. Insert it into the `nodes` file local variable

Putting the code to the test
----------------------------

I used the default Apache web server available on my Mac to test the code.

* I changed the port from 80 to 81 to avoid conflicts
* I started it with `sudo apachectl start`
* I noted the IP of my machine, which is available from Docker containers
* I updated the configuration file: 

```lua
nodes:
  "192.168.1.62:81": 1
#END
```


* I started the Docker Compose containers - `docker compose up`

At this point, I used the admin API to create a route with the new YAML service discovery client:

```bash
curl http://127.0.0.1:9080/apisix/admin/routes/1 -H 'X-API-KEY: edd1c9f034335f136f87ad84b625c8f1' -X PUT -i -d '{
  "uri": "/",
  "upstream": {
    "service_name": "MY-YAML",              # 1
    "type": "roundrobin",
    "discovery_type": "yaml"                # 2-3
  }
}'
```


1. Matches the `service_name` parameter in the `_M.nodes(service_name)` function. It potentially allows returning different nodes based on it. We didn't use it here, so anything works.  

2. The magic happens here. The label must match the name of the discovery folder in `/usr/local/apisix/apisix/discovery/`.  

3. No nodes are set; the service discovery client will dynamically return them

Let's test it:

```
curl localhost:9080
```


It returns the root page served by the Apache Server as expected:

```
<html><body><h1>It works!</h1></body></html>
```


Nitpicking
----------

While the above code works as expected, we can improve it.

### Logging

Relevant logging can help your future self solve nasty bugs in production.

```lua
local core = require("apisix.core")

local function read_file(premature)
    local content, err = util.read_file("/var/apisix/nodes.yaml")
    if not content then
        log.error("Unable to open YAML discovery configuration file: ", err)    -- 1
        return
    end
```


1. Trace the error

### Parameterization

So far, we didn't use any parameters. The configuration file path and the fetch interval are hard-coded. We can do better by making them configurable.

```lua
return {
    type = "object",
    properties = {
        path = { type = "string", default = "/var/apisix/nodes.yaml" },    -- 1
        fetch_interval = { type = "integer", minimum = 1, default = 30 },  -- 1
    },
}
```


1. Parameters with their type and default value. None of them are mandatory.

On the code side, we can use them accordingly.

```lua
local core       = require("apisix.core")
local local_conf = require("apisix.core.config_local").local_conf()

function _M.init_worker()
    local fetch_interval = local_conf.discovery and
                           local_conf.discovery.yaml and
                           local_conf.discovery.yaml.fetch_interval
    ngx_timer_every(fetch_interval, read_file)
```


### Premature

Finally, the `ngx.timer.every` API calls our function with a dedicated `premature` parameter:
> Premature timer expiration happens when the Nginx worker process is trying to shut down, as in an Nginx configuration reload triggered by the `HUP` signal or in an Nginx server shutdown. When the Nginx worker is trying to shut down, one can no longer call `ngx.timer.at` to create new timers with nonzero delays and in that case `ngx.timer.at` will return a "conditional false" value and a string describing the error, that is, "process exiting". -- [ngx.timer.at](https://github.com/openresty/lua-nginx-module#ngxtimerevery)

Let's be a good citizen-developer and handle the parameter accordingly:

```lua
local function read_file(premature)
    if premature then
        return
    end
end
```


Conclusion
----------

Most modern infrastructures are dynamic - servers are cattle, not pets. In this case, it doesn't make much sense to configure the nodes of an upstream statically.

For this reason, Apache APISIX provides service discovery clients.

While it comes with a bundle out-of-the-box, it's possible to write your own by following a couple of steps.

In this article, I described these steps to implement a node registry based on a YAML file.

The complete source code for this post can be found on [Github](https://github.com/ajavageek/apisix-yaml-service-discovery).

**To go further:**

* [Integration service discovery registry](https://apisix.apache.org/docs/apisix/discovery/)
* [Lua Nginx module](https://github.com/openresty/lua-nginx-module)

*Originally published at [A Java Geek](https://blog.frankel.ch/own-service-discovery-client-apisix/) on July 17^th^, 2022*

*[CNCF]: Cloud Native Computing Foundation
*[DNS]: Domain Name Server
