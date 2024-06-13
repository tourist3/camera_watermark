package com.mobileheros.camera.ui.subscribe

import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.drake.net.utils.scopeNetLife
import com.google.gson.Gson
import com.mobileheros.camera.R
import com.mobileheros.camera.databinding.FragmentSubscribeBinding
import com.mobileheros.camera.databinding.ItemProductBinding
import com.mobileheros.camera.event.SubscribeStatusEvent
import com.mobileheros.camera.utils.CommonUtils
import com.mobileheros.camera.utils.Global
import com.mobileheros.camera.utils.PlayBillingHelper
import com.zackratos.ultimatebarx.ultimatebarx.statusBarOnly
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class SubscribeFragment : Fragment() {

    private var _binding: FragmentSubscribeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var isSingle = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isSingle = arguments?.getBoolean("isSingle", false) == true
        _binding = FragmentSubscribeBinding.inflate(inflater, container, false)
        binding.back.visibility = if (isSingle) View.VISIBLE else View.GONE
        binding.back.setOnClickListener { Navigation.findNavController(it).navigateUp() }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        statusBarOnly {
            transparent()
            light = true
        }

        val space = CommonUtils.dp2px(requireContext(), 10f)
        binding.recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.left = space
                outRect.right = space
                outRect.top = space
                outRect.bottom = space
            }
        })
        binding.recyclerView.adapter = BenefitAdapter(products).apply {
            setOnItemClickListener(object : OnItemClickListener{
                override fun onItemClicked(position: Int) {
                    updateText()
                }

            })
        }
        binding.recyclerView.adapter
        binding.join.setOnClickListener {
            activity?.let { _ ->
                products.find { it.checked }?.let {
                    PlayBillingHelper.getInstance(requireActivity().application).processPurchases(
                        requireActivity(),
                        it.parent,
                        it.product.offerToken
                    )
                }
            }
        }

        updateUI()
    }

    private fun updateUI() {
        binding.ownedLayout.visibility = if (Global.isVip) View.VISIBLE else View.GONE
        binding.middleLayout.visibility = if (Global.isVip) View.GONE else View.VISIBLE
        binding.bottomLayout.visibility = if (Global.isVip) View.GONE else View.VISIBLE

        if (!Global.isVip) {
            loadProduct()
        }
    }
    private fun updateText() {
        try {
            products.find { it.checked }?.let { productItemBean ->
                val result = productItemBean.product.pricingPhases.pricingPhaseList.find { it.priceAmountMicros == 0L }
                if (result != null) {
                    binding.joinText.text = getString(R.string.join_free).uppercase()
                } else {
                    binding.joinText.text = getString(R.string.join_now)
                }
            }
        } catch (_:Exception) {

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onEvent(event: SubscribeStatusEvent) {
        if (isSingle) Navigation.findNavController(binding.root).navigateUp() else updateUI()
    }

    private val products: MutableList<ProductItemBean> = mutableListOf()
    private fun loadProduct() {
        scopeNetLife {
            val list =
                PlayBillingHelper.getInstance(requireActivity().application).queryProductDetails()

            products.clear()
            for (i in list.indices) {
                if (list[i].subscriptionOfferDetails.isNullOrEmpty()) continue
                for (j in list[i].subscriptionOfferDetails!!.indices) {
                    products.add(
                        ProductItemBean(
                            list[i].subscriptionOfferDetails!![j],
                            list[i]
                        ).apply {
                            context?.let { transform(it) }
                        })
                }
            }
            val test = products.groupBy { it.product.basePlanId }.values.map { list1 ->
                if (list1.size > 1) {
                    try {
                        list1.firstOrNull { it.product.offerId != null } ?: list1.first()
//                        list1.minByOrNull { it.product.pricingPhases.pricingPhaseList[0].priceAmountMicros }!!
                    } catch (e: Exception) {
                        list1.first()
                    }
                } else list1[0]
            }
            Log.e("test_product", Gson().toJson(test))
            products.clear()
            products.addAll(test)
            if (products.isNotEmpty()) {
                val temp = products.find { it.title == getString(R.string.sub_page_opt_yearly) }
                if (temp != null) {
                    temp.checked = true
                } else {
                    products[0].checked = true
                }
            }
            updateText()
            binding.recyclerView.adapter?.notifyItemRangeChanged(0, products.size)
        }
    }

    class BenefitAdapter(private val data: MutableList<ProductItemBean>) :
        RecyclerView.Adapter<BenefitAdapter.ViewHolder>() {
        class ViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(bean: ProductItemBean, position: Int) {
                binding.period.text = bean.title
                binding.period.setTextColor(binding.root.context.getColor(if (bean.checked) R.color.main else R.color.color_ff313338))
                binding.price.text = bean.subTitle
                binding.price.setTextColor(binding.root.context.getColor(if (bean.checked) R.color.main else R.color.color_ff313338))
                binding.content.background = ContextCompat.getDrawable(
                    binding.content.context,
                    if (bean.checked) R.drawable.bg_product_item_checked else R.drawable.bg_product_item
                )
                binding.discountPrice.visibility = if (bean.discountTitle.isEmpty()) View.GONE else View.VISIBLE
                binding.logo.visibility = if (bean.discountTitle.isEmpty()) View.GONE else View.VISIBLE
                binding.discountPrice.paintFlags = binding.discountPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.discountPrice.text = bean.discountTitle
            }
        }

        var listener: OnItemClickListener? = null
        fun setOnItemClickListener(listener: OnItemClickListener) {
            this.listener = listener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(data[position], position)
            holder.binding.root.setOnClickListener {
                setCheckedPosition(position)
                listener?.onItemClicked(position)
            }
        }

        private fun setCheckedPosition(position: Int) {
            data.find { it.checked }?.checked = false
            data[position].checked = true
            notifyItemRangeChanged(0, itemCount)
        }

    }
}