/**
 * 
 */
function penerimaanCoilMenuClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onPenerimaanCoilMenuClick', 'Admin...'));
}

function prosesCoilMenuClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onProsesCoilMenuClick', 'Admin...'));
}

function produkMenuClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onProdukMenuClick', 'Admin...'));
}

function suratjalanMenuClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onSuratjalanCoilMenuClick', 'Admin...'));
}

function tagihanMenuClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onTagihanCoilMenuClick', 'Admin...'));
}

function customerMenuClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onCustomerCoilMenuClick', 'Admin...'));
}

function inventoryTypeMenuClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onClickInventoryTypeMenu', 'Admin...'));
}

function inventoryCodeMenuClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onClickInventoryCodeMenu', 'Admin...'));
}