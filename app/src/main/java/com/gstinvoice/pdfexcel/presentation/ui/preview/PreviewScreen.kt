package com.gstinvoice.pdfexcel.presentation.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gstinvoice.pdfexcel.data.entity.LineItemEntity
import com.gstinvoice.pdfexcel.data.model.InvoiceWithItems
import com.gstinvoice.pdfexcel.presentation.ui.paywall.PaywallDialog
import com.gstinvoice.pdfexcel.presentation.viewmodel.PreviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    invoiceIds: List<Long>,
    onNavigateBack: () -> Unit,
    onNavigateToExport: (List<Long>) -> Unit,
    viewModel: PreviewViewModel = viewModel()
) {
    val isPremium by viewModel.isPremium.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val totalLineItemsCount by viewModel.totalLineItemsCount.collectAsState()
    val visibleLineItemsCount by viewModel.visibleLineItemsCount.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var showPaywall by remember { mutableStateOf(false) }

    LaunchedEffect(invoiceIds) {
        viewModel.loadInvoices(invoiceIds)
    }

    val visibleLineItems = remember(viewModel.getVisibleLineItems()) {
        viewModel.getVisibleLineItems()
    }

    val isPaywallActive = viewModel.isPaywallActive()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Invoice Preview") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { onNavigateToExport(invoiceIds) }) {
                            Icon(Icons.Default.Share, contentDescription = "Export")
                        }
                    }
                )
                
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search by supplier or invoice number") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true
                )

                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Summary") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Line Items") }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                0 -> SummaryTab(
                    visibleLineItems = visibleLineItems,
                    modifier = Modifier.padding(padding)
                )
                1 -> LineItemsTab(
                    visibleLineItems = visibleLineItems,
                    isPaywallActive = isPaywallActive,
                    totalCount = totalLineItemsCount,
                    visibleCount = visibleLineItemsCount,
                    modifier = Modifier.padding(padding)
                )
            }

            if (isPaywallActive) {
                PaywallBanner(
                    onUpgradeClick = { showPaywall = true },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                )
            }
        }

        if (showPaywall) {
            PaywallDialog(onDismiss = { showPaywall = false })
        }
    }
}

@Composable
fun SummaryTab(
    visibleLineItems: List<Pair<InvoiceWithItems, List<LineItemEntity>>>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(visibleLineItems) { (invoiceWithItems, lineItems) ->
            InvoiceSummaryCard(invoiceWithItems = invoiceWithItems, lineItemCount = lineItems.size)
        }
    }
}

@Composable
fun InvoiceSummaryCard(invoiceWithItems: InvoiceWithItems, lineItemCount: Int) {
    val invoice = invoiceWithItems.invoice
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = invoice.supplierName ?: "Unknown Supplier",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            SummaryRow("Invoice No:", invoice.invoiceNumber ?: "N/A")
            SummaryRow("Date:", invoice.invoiceDate?.toString() ?: "N/A")
            SummaryRow("Supplier GSTIN:", invoice.supplierGstin ?: "N/A")
            SummaryRow("Buyer GSTIN:", invoice.buyerGstin ?: "N/A")
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            SummaryRow("Total Items:", "$lineItemCount")
            SummaryRow("Taxable Value:", "₹${String.format("%.2f", invoice.totalTaxableValue)}")
            SummaryRow("CGST:", "₹${String.format("%.2f", invoice.totalCgst)}")
            SummaryRow("SGST:", "₹${String.format("%.2f", invoice.totalSgst)}")
            SummaryRow("IGST:", "₹${String.format("%.2f", invoice.totalIgst)}")
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Amount:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "₹${String.format("%.2f", invoice.totalAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun LineItemsTab(
    visibleLineItems: List<Pair<InvoiceWithItems, List<LineItemEntity>>>,
    isPaywallActive: Boolean,
    totalCount: Int,
    visibleCount: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (isPaywallActive) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "Showing $visibleCount of $totalCount items. Upgrade to view all.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            visibleLineItems.forEach { (invoiceWithItems, lineItems) ->
                item {
                    Text(
                        text = invoiceWithItems.invoice.supplierName ?: "Unknown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(lineItems) { item ->
                    LineItemCard(item = item)
                }

                // Show blurred items if paywall is active
                if (isPaywallActive && invoiceWithItems.lineItems.size > lineItems.size) {
                    items(2) {
                        Box(modifier = Modifier.blur(8.dp)) {
                            LineItemCard(
                                item = LineItemEntity(
                                    invoiceId = 0,
                                    description = "Sample Item Description",
                                    hsnSac = "12345678",
                                    quantity = 10.0,
                                    rate = 100.0,
                                    taxableValue = 1000.0,
                                    totalAmount = 1180.0
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LineItemCard(item: LineItemEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = item.description ?: "No description",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "HSN/SAC: ${item.hsnSac ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Qty: ${item.quantity ?: 0.0}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Rate: ₹${item.rate ?: 0.0}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Taxable: ₹${String.format("%.2f", item.taxableValue ?: 0.0)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Total: ₹${String.format("%.2f", item.totalAmount ?: 0.0)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun PaywallBanner(onUpgradeClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Upgrade to Premium",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "View all items and export data",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Button(onClick = onUpgradeClick) {
                Text("Upgrade")
            }
        }
    }
}
