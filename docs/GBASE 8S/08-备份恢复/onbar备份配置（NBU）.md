### 安装客户端
在nbu服务器远程安装客户端（该客户端需要在nbu备份策略中存在，如不存在，则在备份策略中增加该客户端）,【root】用户在【nbu server服务器】执行：
```
[root@nbuserver nbu]# /usr/openv/netbackup/bin/install_client_files sftp dbhost01 root
Getting CA certificate details.
NOTE: Depending on the network, this action may take a few minutes.
      To continue without setting up secure communication, press Ctrl+C.
Using CA Certificate fingerprint from master server:
D9:1C:22:D7:FA:0D:D8:2E:86:E9:E5:DF:5C:47:6C:DB:DC:CA:C9:F3
If clients need an authorization token for installation, please specify one here.
Token (leave blank for no authorization token): 

WARNING: Authorization Token was not specified.
         Manual steps may be required before backups and restores can occur.

       dbhost01 ...
Client dbhost01 -- Linux hardware running RedHat2.6.18
Installing NetBackup software on dbhost01 as user root
root@dbhost01's password: 
Connected to dbhost01.

sftp completed successfully.

The root user on dbhost01 must now execute the command
"sh /tmp/bp.10715/client_config [-L]".  The optional argument, "-L",
is used to avoid modification of the client's current bp.conf file.
       dbhost01 install complete

Installation temp files are shredded successfully.

```
【root】用户在【数据库服务器】执行：
```
[root@dbhost01 ~]# sh /tmp/bp.10715/client_config

Checking for required system conditions...

ok be_nb_same_host: inapplicable on linux: skipping
ok unsupported_platform: Passed checks for unsupported platforms.

Checking for recommended system conditions...

ok hotfix_auditor: NetBackup is not installed or there is no hotfix or EEB data present. Skipping HF/EEB Auditor check.

Successfully unpacked /tmp/bp.10715/openv/netbackup/client/Linux/RedHat2.6.18/certcmdTool_for_UNIX.tar.gz.

Checking connectivity to the master server.
NOTE: Depending on the network, this action may take a few minutes.
Connectivity established.

Checking for local CA certificate
Local CA certificate is not found on host. Proceeding with installation.

Getting CA certificate details.
NOTE: Depending on the network, this action may take a few minutes.

CA Certificate received successfully from server nbuserver.

         Subject Name : /CN=nbatd/OU=root@nbuserver/O=vx
           Start Date : Jul 03 16:46:36 2023 GMT
          Expiry Date : Jun 28 18:01:36 2043 GMT
     SHA1 Fingerprint : EE:3C:28:22:6C:30:D1:43:EC:D0:C7:DE:2E:5E:23:47:F8:9E:CD:D5
 CA Certificate State : Not Trusted


Storing CA certificate.
NOTE: Depending on the network, this action may take a few minutes.
The validation of root certificate fingerprint is successful.
CA certificate stored successfully from server nbuserver.

Getting host certificate for hostname dbhost01.
NOTE: Depending on the network, this action may take a few minutes.
Host certificate and certificate revocation list received successfully from server nbuserver.
Installing VRTSnbpck ...
Installed  VRTSnbpck successfully.

Installing VRTSpbx ...
Installed  VRTSpbx successfully.

Installing VRTSnbclt ...
Installed  VRTSnbclt successfully.

Installing VRTSnbjre ...
Installed  VRTSnbjre successfully.

Installing VRTSnbjava ...
Installed  VRTSnbjava successfully.

Installing VRTSpddea ...
Installed  VRTSpddea successfully.

Installing VRTSnbcfg ...
Installed  VRTSnbcfg successfully.


Client install complete.

INF OTHER - EXIT STATUS = 0
```
### 配置客户端
【root】用户在数据库服务器执行：
```
[root@dbhost02 ~]# cd /usr/openv/netbackup/bin
[root@dbhost02 bin]# ./informix_config

Please specify the Informix instance home path name:
/opt/gbase


Changing informix scripts' file ownership and group.

Linking /usr/lib/ibsad001.so to /usr/openv/netbackup/bin/infxbsa.so64.
```
【gbasedbt】用户在数据库服务器执行：
```
sed -i "s#^BAR_BSALIB_PATH.*#BAR_BSALIB_PATH /usr/openv/netbackup/bin/infxbsa.so64#g" $GBASEDBTDIR/etc/$ONCONFIG
```
参考文档【[GBase 8s 备份与恢复指南.pdf](https://www.dbboys.com/dl/gbase8s/docs/Backup_and_Restore_Guide.pdf)】