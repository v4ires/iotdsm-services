cp /var/log/osquery/osqueryd.results.log /home/viniciusaires/iot-repository/scripts/osquery/osquery.d.result.log.$1.$2.$3.$4.$5.result
chown -R viniciusaires:allusers /var/log/osquery/osqueryd.results.log /home/viniciusaires/iot-repository/scripts/osquery/osquery.d.result.log.$1.$2.$3.$4.$5.result
rm -r /var/log/osquery/*
