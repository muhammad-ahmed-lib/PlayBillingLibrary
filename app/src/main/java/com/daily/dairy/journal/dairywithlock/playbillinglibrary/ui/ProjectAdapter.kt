package com.daily.dairy.journal.dairywithlock.playbillinglibrary.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daily.dairy.journal.dairywithlock.playbillinglibrary.databinding.ItemprojectBinding
import com.daily.dairy.journal.dairywithlock.playbillinglibrary.dataclasses.Project

class ProjectAdapter(private val projects: List<Project>) :
    RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    inner class ProjectViewHolder(val binding: ItemprojectBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding = ItemprojectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projects[position]
        holder.binding.titleTv.text = project.title
        holder.binding.openBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(project.link)
            }
            holder.binding.root.context.startActivity(intent)
        }
    }

    override fun getItemCount() = projects.size
}
