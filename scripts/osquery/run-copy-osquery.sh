cp /var/log/osquery/osqueryd.results.log /home/viniciusaires/iotdsm-edu.usp.icmc.lasdpc.iotdsm.services/scripts/osquery/osquery.d.result.log.$1.$2.result
chown -R viniciusaires:allusers /var/log/osquery/osqueryd.results.log /home/viniciusaires/iotdsm-edu.usp.icmc.lasdpc.iotdsm.services/scripts/osquery/osquery.d.result.log.$1.$2.result
rm -r /var/log/osquery/*
