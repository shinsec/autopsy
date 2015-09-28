/*
 * Autopsy Forensic Browser
 *
 * Copyright 2014 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sleuthkit.autopsy.timeline.ui.detailview.tree;

import java.util.Comparator;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javafx.collections.FXCollections;
import javafx.scene.control.TreeItem;
import org.sleuthkit.autopsy.timeline.datamodel.EventBundle;

/**
 *
 */
class EventDescriptionTreeItem extends NavTreeItem {

    /**
     * maps a description to the child item of this item with that description
     */
    private final Map<String, EventDescriptionTreeItem> childMap = new ConcurrentHashMap<>();
    private final EventBundle bundle;

    public EventBundle getEventBundle() {
        return bundle;
    }

    EventDescriptionTreeItem(EventBundle g) {
        bundle = g;
        setValue(new NavTreeNode(g.getEventType().getBaseType(), g.getDescription(), g.getDescriptionLOD(), g.getEventIDs().size()));
    }

    @Override
    public int getCount() {
        return getValue().getCount();
    }

    public void insert(Deque<EventBundle> path) {
        EventBundle head = path.removeFirst();
        EventDescriptionTreeItem treeItem = childMap.get(head.getDescription());
        if (treeItem == null) {
            treeItem = new EventDescriptionTreeItem(head);
            treeItem.setExpanded(true);
            childMap.put(head.getDescription(), treeItem);
            getChildren().add(treeItem);
            FXCollections.sort(getChildren(), TreeComparator.Description);
        }

        if (path.isEmpty() == false) {
            treeItem.insert(path);
        }
    }

    @Override
    public void resort(Comparator<TreeItem<NavTreeNode>> comp) {
        FXCollections.sort(getChildren(), comp);
    }

    @Override
    public TreeItem<NavTreeNode> findTreeItemForEvent(EventBundle t) {

        if (getValue().getType().getBaseType() == t.getEventType().getBaseType() && getValue().getDescription().equals(t.getDescription())) {
            return this;
        }
        return null;
    }
}
