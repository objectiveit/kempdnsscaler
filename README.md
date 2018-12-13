# kempdnsscaler

_AWS Lambda_ function to manage list of _Real Servers_ for _Virtual Services_ in the _LoadBalancer_ (currently supported [KEMP LoadMaster](https://kemptechnologies.com/) only).

## Installation

Please ensure you have the [AWS CLI](https://aws.amazon.com/cli) installed and configured with [credentials](http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html).

To deploy _AWS Lambda_ function please utilize installation script: `install.sh` or `install.bat` - it will build the project, create _AWS Lambda_ service execution role and finally will deploy the function.

## Details

Class `de.objectiveit.kempdnsscaler.VSManager` implements AWS request handler interface and consumes _de.objectiveit.kempdnsscaler.model.VSRequest_ DTO as input, which should look something like this:

```json
{
    "loadBalancerURL": "https://some.loadbalancer.de",
    "credentials": {
        "login": "admin",
        "password": "verysecret"
    },
    "vs": {
        "ip": "1.2.3.4",
        "port": 8080,
        "protocol": "tcp"
    },
    "rsIPs": [
        "1.2.3.10",
        "1.2.3.11",
        "rs.realserver.com"
    ],
    "rsPort": 80
}
```

Please find parameters list and their descriptions in the table below:

| Parameter | Description | Example |
|:---|------|:----:|
| `loadBalancerURL` | _LoadBalancer_ base URL, might be either FQDN or IP based | `"https://some.loadbalancer.de"`, `"http://12.13.14.15` |
| `credentials` | _LoadBalancer_ login and password, in case of _KEMP LoadMaster_ it is the [REST API basic auth credentials >>](https://support.kemptechnologies.com/hc/en-us/articles/203863435-RESTful-API#MadCap_TOC_6_2) | `{ "login": "admin", "password": "verysecret" }` |
| `vs` | Parameters of the _Virtual Service_ to manage: IP, port and protocol | `{ "ip": "1.2.3.4", "port": 8080, "protocol": "tcp" }` |
| `rsIPs` | Array of _Real Service_ FQDNs/IPs to set up for the _VS_, in case of the fully successful operation (no partial _RS_ failures) - _VS_ will be configured only with these _RS_ IPs - unneeded ones will be removed, required ones will be added. For the FQDN entries `nslookup` will be utilized to prepare list of IP addresses to add (details below) | `[ "1.2.3.10", "1.2.3.11", "rs.realserver.com" ]` |

### nslookup

As it is mentioned above for FQDNs `nslookup` will be utilized, so e.g. for the RSs list below:

```
    "rsIPs": [ "1.2.3.10", "1.2.3.11", "rs.realserver.com" ]
```

In case when there are 3 IP addresses behind `rs.realserver.com`:

```sh
$ nslookup rs.realserver.com
Server: some.server.com
Address: 11.22.33.44

Name: rs12345.realserver.de
Addresses: 1.2.4.1
1.2.4.2
1.2.4.3
Aliases: rs.realserver.com
```

Result list of IP addresses to configure will be: `1.2.3.10, 1.2.3.11, 1.2.4.1, 1.2.4.2, 1.2.4.3`
