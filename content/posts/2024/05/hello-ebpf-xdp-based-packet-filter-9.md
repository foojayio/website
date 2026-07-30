---
title: "Hello eBPF: XDP-based Packet Filter (9)"
slug: "hello-ebpf-xdp-based-packet-filter-9"
date: "2024-05-13T07:13:38+00:00"
lastmod: "2024-05-13T07:13:39+00:00"
description: "Learn about new support for XDP to create a simple package blocker for eBPF."
authors:
  - "johannes-bechberger"
image: "/images/posts/2024/05/hello-ebpf-xdp-based-packet-filter-9/xdp_filter-1-2000x1005-1.png"
categories:
  - "Tools"
tags:
related_posts:
  - "hello-ebpf-developing-ebpf-apps-in-java-1"
  - "hello-ebpf-recording-data-in-basic-ebpf-maps-2"
  - "hello-ebpf-recording-data-in-event-buffers-3"
  - "hello-ebpf-generating-c-code-8"
enlighterjs: true
frozen: false
---

**Welcome back to my [series on ebpf](https://mostlynerdless.de/blog/tag/hello-ebpf/). In the last blog post, we learned how [annotation processors can](https://mostlynerdless.de/blog/2024/04/09/hello-ebpf-generating-c-code-8/)generate C code, simplifying writing eBPF applications.**

This week, we'll use this work together with new support for XDP to create a simple package blocker for eBPF ([GitHub](https://github.com/parttimenerd/hello-ebpf/blob/main/bpf/src/main/java/me/bechberger/ebpf/samples/XDPPacketFilter.java)):

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">./run_bpf.sh XDPPacketFilter twitter.com</pre>

This blocks all incoming IPv4 packages from `twitter.com`. We see how it works in this blog post. First, we start with some background on networking and explain what XDP is.

Network Packet {#h2-0-network-packet}
-------------------------------------

All networking is packet-based, with multiple layers of protocol from shared medium (e.g., Ethernet) to application level (e.g., HTTP):
![](https://mostlynerdless.de/wp-content/uploads/2024/04/network_stack-2000x517.png)

[Ethernet](https://en.wikipedia.org/wiki/Ethernet) is the lowest-level protocol, with all packets coming to and from network interfaces being ethernet packets. The ethernet header contains the "physical" MAC address of both the source and destination of the package, combined with the protocol number of the next level protocol. We can represent it in C as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="cpp" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">struct ethhdr {
    unsigned char h_dest[6];
    unsigned char h_source[6];
    __be16 h_proto;
};</pre>

Today, Ethernet is routed on switch level, but it was initially used to communicate between devices that shared the same medium, typically cable.

Above the Ethernet protocol sit multiple protocols, but we're focusing here on the [Internet Protocol](https://en.wikipedia.org/wiki/Internet_Protocol) (IP) with protocol type 0x0800. The IP protocol comes in two common variants, IPv4 and IPv6, and is used to communicate between devices on the whole internet. Although IPv6 has many advantages, IPv4 is still commonly used, and we're focusing on this variant in the following section to keep it simple. IP datagrams are typically fragmented into multiple smaller IP packets. An IPv4 header consists of the following parts:

<pre class="EnlighterJSRAW" data-enlighter-language="cpp" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">struct iphdr {
    __u8 ihl: 4; // number of 32-bits in the header
    __u8 version: 4; // 4 (IPv4), 6 (IPv6)
    __u8 tos; // "priority" of the packet
    __be16 tot_len; // size of packet (header + data) in bytes
    __be16 id; // id of the datagram that this fragment belongs to
    // offset of this packet fragment in the unfragmented datagram
    __be16 frag_off;
    // hop count, router decrement it and drop package if ttl = 0
    __u8 ttl;
    // next level protocol
    __u8 protocol;
    // checksum, so that sum of all header 32-bit words is 0xFFFF
    __sum16 check;
    // source and destination address
    // (might be changed in transit 
    //  due to network address translation)
    struct {
        __be32 saddr;
        __be32 daddr;
    } addrs;
};</pre>

This misses the last field officially specified field, the options field, but it is, according to Wikipedia, usually not used:
> The [options field](https://en.wikipedia.org/wiki/Internet_Protocol_Options) is not often used. Packets containing [some options may be considered as dangerous](https://en.wikipedia.org/wiki/Internet_Protocol_Options_Considerations) by some routers and be blocked.^[](https://en.wikipedia.org/wiki/Internet_Protocol_version_4#cite_note-40)^
> [wikipedia](https://en.wikipedia.org/wiki/Internet_Protocol_version_4#Options)

Above the IP protocol is the TCP protocol, which essentially adds ports and acknowledged package delivery, and on the web, the topmost layer is usually HTTP, which adds URL paths and more.

eXpress Data Path (XDP) {#h2-1-express-data-path-xdp}
-----------------------------------------------------

XDP is one of the most essential parts of the eBPF kernel land. It allows users to write firewalls, load balancers, and more, such as the packet filter of this blog post. To quote Jonathan Corbet:
> The core idea behind the XDP initiative is to get the network stack out of the way as much as possible. While the network stack is highly flexible, XDP is built around a bare-bones packet transport that is as fast as it can be. When a decision needs to be made or a packet must be modified, XDP will provide a hook for a user-supplied BPF program to do the work. The result combines minimal overhead with a great deal of flexibility, at the cost of a little "some assembly required" label on the relevant man pages.
> [Accelerating networking with AF_XDP](https://lwn.net/Articles/750845/)

The eBPF hooks attached to a specific network interface can inspect and modify the incoming packages, let them pass, drop, or send them back. A basic eBPF program that drops all packages looks, for example, like the following:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">SEC("xdp")
int xdp_drop(struct xdp_md *ctx) {
    return XDP_DROP:
}</pre>

*But please don't attach this program, as it would also drop Address Resolution Protocol (ARP) packages,* which other members of your local ethernet network can map IP addresses to MAC addresses. Dropping all ARP packages*can effectively disconnect your machine from the local network*.

The passed `xdp_md` object contains the package content and some metadata:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">struct xdp_md {
    __u32 data;
    __u32 data_end;
    __u32 data_meta;
    __u32 ingress_ifindex;
    __u32 rx_queue_index;
    __u32 egress_ifindex;
};</pre>

We're just focusing on the content that can be found between `data` and `data_end`. In fact, the header data structures I showed you in the previous section are precisely the structures that describe the content.

Armed with this knowledge, we can now create a package filter:

Writing a Packet Filter {#h2-2-writing-a-packet-filter}
-------------------------------------------------------

The basic structure of our packet filter application consists of a Java part that handles the configuration and logging and an eBPF part that uses an XDP hook that is called for every received packet. As explained above, The XDP hook decides what to do with every packet. So the structures are as follows:
![](https://mostlynerdless.de/wp-content/uploads/2024/04/xdp_filter-1-2000x1005.png)

We start with the definition of eBPF for collecting statistics, blocked packets per IP address, and the configuration of the blocked IP addresses:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@BPFMapDefinition(maxEntries = 256 * 4096)
BPFHashMap&lt;Integer, Boolean&gt; blockedIPs;

@BPFMapDefinition(maxEntries = 256 * 4096)
BPFHashMap&lt;Integer, Integer&gt; blockingStats;</pre>

Now we move on to the eBPF program that checks for the IPv4 addresses and drops the packet if the address is in the `blockedIPs` map (based on the program from a blog post of [sematext](https://sematext.com/blog/ebpf-and-xdp-for-processing-packets-at-bare-metal-speed/)):

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">#include &lt;vmlinux.h&gt;
#include &lt;bpf/bpf_helpers.h&gt;
#include &lt;bpf/bpf_endian.h&gt;

// protocol numbers 
// copied from the linux kernel
#define ETH_P_8021Q 0x8100
#define ETH_P_8021AD 0x88A8
#define ETH_P_IP 0x08
#define ETH_P_IPV6 0x86DD
#define ETH_P_ARP 0x0806

SEC("xdp")
int xdp_pass(struct xdp_md *ctx) {
    // the package  
    void *end = (void *)(long)ctx-&gt;data_end;
    void *data = (void *)(long)ctx-&gt;data;
    u32 ip_src;
    u64 offset;
    u16 eth_type;

    struct ethhdr *eth = data;
    offset = sizeof(*eth);

    if (data + offset &gt; end) {
        // ethernet package header is incomplete
        return XDP_ABORTED;
    }
    eth_type = eth-&gt;h_proto;

    /* handle VLAN tagged packet */
    // we use bpf_htons for the check to convert
    // from hardware to network endianess
    if (eth_type == bpf_htons(ETH_P_8021Q) || 
          eth_type == bpf_htons(ETH_P_8021AD)) {
        struct vlan_hdr *vlan_hdr;

        vlan_hdr = (void *)eth + offset;
        offset += sizeof(*vlan_hdr);
        if ((void *)eth + offset &gt; end) {
            // ethernet package header is incomplete
            return false;
        }
        eth_type = vlan_hdr-&gt;h_vlan_encapsulated_proto;
    }

    /* let's only handle IPv4 addresses */
    if (eth_type != bpf_htons(ETH_P_IP)) {
        return XDP_PASS;
    }

    // get the IPv4 header
    struct iphdr *iph = data + offset;
    offset += sizeof(struct iphdr);

    // make sure the bytes you want to read are 
    // within the packet's range before reading them
    if (iph + 1 &gt; end) {
        return XDP_ABORTED;
    }
    ip_src = iph-&gt;saddr;

    // find entry in block list
    void* ret = (void*)bpf_map_lookup_elem(&amp;blockedIPs, &amp;ip_src);
    if (!ret) {
        // IP not in blocked list
        return XDP_PASS;
    }

    // count the number of blocked packages per IP address
    s32* counter = bpf_map_lookup_elem(&amp;blockingStats, &amp;ip_src);
    if (counter) {
        // use atomics to prevent a race condition when a packet
        // from the same IP address is received on two
        // different cores at the same time
        // (thanks Dylan Reimerink for catching this bug)
        __sync_fetch_and_add(counter, 1);
    } else {
        u64 value = 1;
        bpf_map_update_elem(&amp;blockingStats, &amp;ip_src, 
                            &amp;value, BPF_ANY);
    }

    return XDP_DROP;
}</pre>

Now we use it with some [picocli](https://picocli.info/)-based command line handling to build our application:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@BPF(license = "GPL")
@Command(name = "XDPPacketFilter", 
         mixinStandardHelpOptions = true, 
         description = "Use XDP to block " +
                       "incoming IPv4 packages from a URLs")
public abstract class XDPPacketFilter 
  extends BPFProgram implements Runnable {

    // maps, ...

    private static final String EBPF_PROGRAM = """
            // ...
            """;

    @Parameters(arity = "1..*", description = "URLs to block")
    private String[] blockedUrls;

    @Option(names = "--run-url-retrieve-loop", 
        description = "Try to retrieve the content " + 
                      "of the first URL in a loop")
    private boolean runURLRetrieveLoop;

    private Map&lt;Integer, String&gt; ipToUrlMap;

    void setupBlockedIPMap() {
        ipToUrlMap = Arrays.stream(blockedUrls).flatMap(url -&gt; {
            try {
                // Resolve the URL to the related IP addresses
                return Arrays.stream(
                    InetAddress.getAllByName(url))
                               .map(addr -&gt; 
                       // convert the IP address to numbers
                       Map.entry(XDPUtil.ipAddressToInt(addr), url));
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toMap(
             Map.Entry::getKey, 
             Map.Entry::getValue));
        ipToUrlMap.keySet().forEach(ip -&gt; {
            // put the IP addresses in the map
            blockedIPs.put(ip, true);
        });
    }

    // print the content of blockingStats
    void printBlockedLog() {
        out.println("Blocked packages:");
        blockingStats.forEach((ip, count) -&gt; {
            out.println("  Blocked " + count + " packages from " +
                    XDPUtil.intToIpAddress(ip) +
                    " (" + ipToUrlMap.get(ip) + ")");
        });
    }

    @Override
    public void run() {
        setupBlockedIPMap();
        if (runURLRetrieveLoop) {
            XDPUtil.openURLInLoop(blockedUrls[0]);
        }
        xdpAttach(getProgramByName("xdp_pass"), 
            XDPUtil.getNetworkInterfaceIndex());
        // print the blocking statistics every second
        while (true) {
            printBlockedLog();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        try (XDPPacketFilter program = 
              BPFProgram.load(XDPPacketFilter.class)) {
            var cmd = new CommandLine(program);
            cmd.parseArgs(args);
            if (cmd.isUsageHelpRequested()) {
                cmd.usage(out);
                return;
            }
            program.run();
        }
    }
}</pre>

This is all we need, now we can use it:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># block twitter.com and log in the background
&gt; ./run_bpf.sh XDPPacketFilter twitter.com &gt; log.txt &amp;
# try to access twitter.com with a timeout of 5 seconds
&gt; wget twitter.com --timeout 5
URL transformed to HTTPS due to an HSTS policy
--2024-04-22 13:28:29--  https://twitter.com/
Resolving twitter.com (twitter.com)... 104.244.42.65
Connecting to twitter.com (twitter.com)|104.244.42.65|:443... failed: Connection timed out.
Retrying.

--2024-04-22 13:28:36--  (try: 2)  https://twitter.com/
Connecting to twitter.com (twitter.com)|104.244.42.65|:443... failed: Connection timed out.
Retrying.

--2024-04-22 13:28:43--  (try: 3)  https://twitter.com/
Connecting to twitter.com (twitter.com)|104.244.42.65|:443... failed: Connection timed out.
Retrying.

# and so on</pre>

So we can't access twitter.com anymore till we stop our application.

Conclusion {#h2-3-conclusion}
-----------------------------

Using XDP and eBPF, we can create a partial packet filter that is easily extended into a firewall and blocks incoming packets. The filtering overhead is low, as the packets are processed directly in the kernel. Using hello-ebpf, we can wrap the filter program in a neat command-line application.

But this is only the starting point; we can use a few more XDP features to create a fast load-balancer and add support for cgroups to create a proper firewall, filtering out-going packages too. The following two blog posts will be about this, so see you in two weeks' time.

*This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com/), making profiling and debugging easier for everyone. This article first appeared on my personal blog [mostlynerdless.de](https://mostlynerdless.de/blog/2024/04/22/hello-ebpf-xdp-based-packet-filter-9/).*
